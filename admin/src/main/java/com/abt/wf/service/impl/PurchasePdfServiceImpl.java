package com.abt.wf.service.impl;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.write.metadata.WriteSheet;
import cn.idev.excel.write.metadata.fill.FillConfig;
import com.abt.common.ExcelUtil;
import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.wf.entity.PurchaseApplyDetail;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchasePdfSignatureFile;
import com.abt.wf.model.PurchasePdfSignatures;
import com.abt.wf.repository.PurchasePdfSignatureRepository;
import com.abt.wf.listener.PurchaseNameMergeHandler;
import com.abt.wf.repository.PurchaseApplyMainRepository;
import com.abt.wf.service.PurchasePdfService;
import com.abt.wf.service.SignatureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;
import java.util.Objects;

/** 复用采购 Excel 模板的数据填充与样式，POI 读取后直接绘制到同一个 PDFBox 文档。 */
@Service
@Slf4j
public class PurchasePdfServiceImpl implements PurchasePdfService {
    private final PurchaseApplyMainRepository repository;
    private final SignatureService signatureService;
    private final PurchasePdfSignatureRepository signatureRepository;
    private final Resource font;
    private final String applyTemplate;
    private final String acceptTemplate;

    public PurchasePdfServiceImpl(PurchaseApplyMainRepository repository, SignatureService signatureService,
            PurchasePdfSignatureRepository signatureRepository,
            @Value("${abt.pur.pdf.font:classpath:fonts/purchase/NotoSerifSC-Regular.ttf}") Resource font,
            @Value("${abt.pur.excel.template}") String applyTemplate,
            @Value("${abt.pur.accept.excel.template}") String acceptTemplate) {
        this.repository = repository;
        this.signatureService = signatureService;
        this.signatureRepository = signatureRepository;
        this.font = font;
        this.applyTemplate = applyTemplate;
        this.acceptTemplate = acceptTemplate;
    }

    @Override
    public byte[] createPdf(String id) throws IOException {
        return single(load(id), false);
    }

    @Override
    public byte[] createAcceptPdf(String id) throws IOException {
        PurchaseApplyMain main = load(id);
        if (!main.isFinished()) {
            throw new BusinessException("采购申请流程未完成，无法生成验收单!");
        }
        if (!main.isAccepted()) {
            throw new BusinessException("采购申请单未验收，请申请人验收后再生成验收单");
        }
        return single(main, true);
    }

    private PurchaseApplyMain load(String id) {
        TokenUtil.getUseridFromAuthToken();
        PurchaseApplyMain main = repository.findByIdWithDetails(id);
        if (main == null) throw new BusinessException("未查询到采购申请(id=" + id + ")");
        return main;
    }

    private byte[] single(PurchaseApplyMain main, boolean acceptance) throws IOException {
        try (PDDocument document = new PDDocument(MemoryUsageSetting.setupTempFileOnly());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDType0Font pdfFont = loadFont(document);
            append(document, pdfFont, main, acceptance, findSignatures(List.of(main)));
            document.save(output);
            log.info("生成新版采购 PDF，单据={}，验收单={}", main.getId(), acceptance);
            return output.toByteArray();
        }
    }

    @Override
    public Path createDocumentPdf(PurchaseApplyMain main, Path target) throws IOException {
        return createDocumentPdf(main, target, findSignatures(List.of(main)));
    }

    @Override
    public Path createDocumentPdf(PurchaseApplyMain main, Path target, PurchasePdfSignatures signatures) throws IOException {
        Files.createDirectories(target.toAbsolutePath().getParent());
        try (PDDocument document = new PDDocument(MemoryUsageSetting.setupTempFileOnly())) {
            PDType0Font pdfFont = loadFont(document);
            append(document, pdfFont, main, false, signatures);
            if (main.isFinished() && main.isAccepted()) {
                // append 每次从新页开始；横纵向页面属于同一文档，不生成或合并中间 PDF。
                append(document, pdfFont, main, true, signatures);
            }
            document.save(target.toFile());
            log.info("生成新版采购整单 PDF，单据={}，页数={}", main.getId(), document.getNumberOfPages());
        }
        return target;
    }

    private PDType0Font loadFont(PDDocument document) throws IOException {
        if (!font.exists()) throw new BusinessException("采购 PDF 中文字体不存在，请联系管理员");
        try (var input = font.getInputStream()) {
            return PDType0Font.load(document, input, true);
        }
    }

    @Override
    public PurchasePdfSignatures findSignatures(List<PurchaseApplyMain> mains) {
        Set<String> users = new LinkedHashSet<>();
        for (PurchaseApplyMain main : mains) {
            addUser(users, main.getCreateUserid());
            if (main.getManagerCheckDate() != null) addUser(users, main.getManagerUserid());
            if (main.getLeaderCheckDate() != null) addUser(users, main.getLeaderUserid());
            if (main.getPurchaserCheckDate() != null || (main.isAccepted() && main.isFinished())) {
                addUser(users, main.getPurchaser());
            }
            if (main.getCeoCheckDate() != null) addUser(users, main.getCeo());
            if (main.getChiefCheckDate() != null) addUser(users, main.getChief());
        }
        List<String> ids = new ArrayList<>(users);
        List<PurchasePdfSignatureFile> signatures = new ArrayList<>();
        // 正常一批 100 单至多 600 人，只查询一次；额外分片避免调用规模扩大后超过 SQL Server 参数限制。
        final int querySize = 1000;
        for (int start = 0; start < ids.size(); start += querySize) {
            signatures.addAll(signatureRepository.findList(ids.subList(start, Math.min(start + querySize, ids.size()))));
        }
        return new PurchasePdfSignatures(signatures);
    }

    private void addUser(Set<String> users, String userid) {
        if (StringUtils.isNotBlank(userid)) users.add(userid);
    }

    /** 填充结果仅保存在当前单据的内存中，不产生 Excel 或中间 PDF 文件。 */
    private void append(PDDocument document, PDType0Font pdfFont, PurchaseApplyMain main, boolean acceptance, PurchasePdfSignatures signatures)
            throws IOException {
        String template = acceptance ? acceptTemplate : applyTemplate;
        if (!Files.isRegularFile(Path.of(template))) {
            throw new BusinessException(acceptance ? "采购验收单模板不存在，请联系管理员" : "采购单模板不存在，请联系管理员");
        }
        List<PurchaseApplyDetail> details = new ArrayList<>(Objects.requireNonNullElse(main.getDetails(), List.of()));
        details.sort(Comparator.comparing(PurchaseApplyDetail::getSortNo, Comparator.nullsLast(Comparator.naturalOrder())));
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            try (ExcelWriter writer = EasyExcel.write(output).withTemplate(template).build()) {
                WriteSheet sheet = acceptance ? EasyExcel.writerSheet().build()
                        : EasyExcel.writerSheet().registerWriteHandler(new PurchaseNameMergeHandler()).build();
                writer.fill(details, FillConfig.builder().forceNewRow(true).build(), sheet);
                writer.fill(acceptance ? acceptanceValues(main, signatures) : applicationValues(main, signatures), sheet);
            }
            try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(output.toByteArray()))) {
                new PurchasePdfTemplateRenderer(document, pdfFont, workbook, acceptance).render();
            }
        }
    }

    private Map<String, Object> applicationValues(PurchaseApplyMain main, PurchasePdfSignatures signatures) throws IOException {
        Map<String, Object> values = new HashMap<>();
        values.put("createDate", TimeUtil.toYYYY_MM_DDString(main.getCreateDate()));
        values.put("managerUpdate", TimeUtil.toYYYY_MM_DDString(main.getManagerCheckDate()));
        values.put("leaderUpdate", TimeUtil.toYYYY_MM_DDString(main.getLeaderCheckDate()));
        values.put("purchaserUpdate", TimeUtil.toYYYY_MM_DDString(main.getPurchaserCheckDate()));
        values.put("ceoUpdate", TimeUtil.toYYYY_MM_DDString(main.getCeoCheckDate()));
        values.put("chiefUpdate", TimeUtil.toYYYY_MM_DDString(main.getChiefCheckDate()));
        values.put("createUserSig", signature(main.getCreateUserid(), "/", signatures));
        if (main.getManagerCheckDate() != null) values.put("managerSig", signature(main.getManagerUserid(), "/", signatures));
        if (main.getLeaderCheckDate() != null) values.put("leaderSig", signature(main.getLeaderUserid(), "/", signatures));
        if (main.getPurchaserCheckDate() != null) values.put("purchaserSig", signature(main.getPurchaser(), "/", signatures));
        if (main.getCeoCheckDate() != null) {
            values.put("ceoLabel", "总经理/日期：");
            values.put("ceoSig", signature(main.getCeo(), "/", signatures));
        }
        if (main.getChiefCheckDate() != null) {
            values.put("chiefLabel", "董事长/日期：");
            values.put("chiefSig", signature(main.getChief(), "/", signatures));
        }
        values.put("total", main.getCost());
        return values;
    }

    private Map<String, Object> acceptanceValues(PurchaseApplyMain main, PurchasePdfSignatures signatures) throws IOException {
        Map<String, Object> values = new HashMap<>();
        values.put("acceptDate", TimeUtil.toYYYY_MM_DDString(main.getAcceptDate()));
        // 与原模板保持一致：“申请人”栏实际使用采购人签名，“验收人”栏使用创建人签名。
        values.put("purchaserSig", signature(main.getPurchaser(), "", signatures));
        values.put("createUserSig", signature(main.getCreateUserid(), "", signatures));
        return values;
    }

    private Object signature(String userid, String fallback, PurchasePdfSignatures signatures) throws IOException {
        if (StringUtils.isBlank(userid)) return fallback;
        PurchasePdfSignatureFile signature = signatures.findByUserid(userid);
        if (signature == null || StringUtils.isBlank(signature.fileName())) return fallback;
        WriteCellData<Void> image = ExcelUtil.createImageData(Path.of(signatureService.getSignatureDir(),
                signature.fileName()).toFile());
        return image == null ? fallback : image;
    }
}
