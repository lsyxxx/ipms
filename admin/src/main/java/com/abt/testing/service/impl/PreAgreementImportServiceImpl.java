package com.abt.testing.service.impl;

import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.sys.repository.CustomerInfoRepository;
import com.abt.sys.service.CompanyService;
import com.abt.testing.entity.Agreement;
import com.abt.testing.entity.EnumLib;
import com.abt.testing.model.PreAgreementImportError;
import com.abt.testing.model.PreAgreementImportResult;
import com.abt.testing.repository.AgreementRepository;
import com.abt.testing.service.AgreementService;
import com.abt.testing.service.PreAgreementImportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

/** 先校验全部数据，再在同一事务中保存预登记合同。 */
@Service
@Slf4j
public class PreAgreementImportServiceImpl implements PreAgreementImportService {
    private static final String[] HEADERS = {"合同编号", "合同名称", "甲方单位", "乙方单位"};
    private static final int MAX_ROWS = 1000;
    private static final int QUERY_BATCH_SIZE = 500;
    // T_Agreement 为 Chinese_PRC_CI_AS 的 varchar，长度按代码页 936 字节计算。
    private static final Charset DATABASE_CHARSET = Charset.forName("GBK");

    private final AgreementRepository agreementRepository;
    private final CustomerInfoRepository customerRepository;
    private final CompanyService companyService;
    private final AgreementService agreementService;
    private final List<EnumLib> agreementTypes;

    public PreAgreementImportServiceImpl(AgreementRepository agreementRepository,
            CustomerInfoRepository customerRepository, CompanyService companyService,
            AgreementService agreementService,
            @Qualifier("agreementTypeEnumList") List<EnumLib> agreementTypes) {
        this.agreementRepository = agreementRepository;
        this.customerRepository = customerRepository;
        this.companyService = companyService;
        this.agreementService = agreementService;
        this.agreementTypes = agreementTypes;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public PreAgreementImportResult save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择非空的 Excel 文件");
        }
        if (file.getSize() > 5 * 1024 * 1024 || file.getOriginalFilename() == null
                || !file.getOriginalFilename().toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            throw new BusinessException("仅支持不超过 5MB 的 .xlsx 文件");
        }
        PreAgreementImportResult result = new PreAgreementImportResult();
        List<ImportRow> rows = readRows(file, result);
        if (result.getErrors().stream().anyMatch(e -> e.getRowNumber() == 1)) {
            return result;
        }
        List<EnumLib> types = agreementTypes.stream()
                .filter(t -> "检验检测服务类".equals(t.getFname())).toList();
        if (types.size() != 1 || !fitsDatabase(types.get(0).getFid(), 20)) {
            throw new BusinessException("合同类型字典缺失、重名或编码不符合数据库要求，请联系管理员");
        }
        Map<String, List<CustomerInfo>> clients = byName(customerRepository
                .findAllByIsAvtiveIs(CustomerInfoRepository.isAtiveTrue));
        Map<String, List<CustomerInfo>> companies = byName(companyService.findYCompanyList());
        Map<String, List<ImportRow>> codes = rows.stream()
                .collect(Collectors.groupingBy(r -> codeKey(r.values[0])));
        Set<String> existing = findExistingCodes(rows);
        List<Agreement> entities = new ArrayList<>();
        for (ImportRow row : rows) {
            String code = row.values[0];
            validateText(result, row, 0, 20);
            validateText(result, row, 1, 200);
            CustomerInfo client = match(result, row, 2, clients);
            CustomerInfo company = match(result, row, 3, companies);
            if (!StringUtils.isBlank(code) && codes.get(codeKey(code)).size() > 1) {
                error(result, row.number, 0, code, "文件内合同编号重复");
            }
            if (existing.contains(codeKey(code))) {
                error(result, row.number, 0, code, "合同编号已存在");
            }
            if (company != null && !StringUtils.isBlank(code)) {
                try {
                    agreementService.validatePreAgreementCode(code, company.getId());
                } catch (BusinessException ex) {
                    error(result, row.number, 0, code, ex.getMessage());
                }
            }
            Agreement agreement = new Agreement();
            agreement.setAgreementCode(code);
            agreement.setAgreementName(row.values[1]);
            agreement.setAgreementType(types.get(0).getFid());
            if (client != null) agreement.setJCompanyId(client.getId());
            if (company != null) agreement.setYCompanyId(company.getId());
            agreement.doFinish();
            entities.add(agreement);
        }
        if (!result.getErrors().isEmpty()) {
            log.info("预登记合同导入校验失败，数据行数：{}，错误数：{}", rows.size(), result.getErrors().size());
            return result;
        }
        // 数据库 Operator/OperatorName 不可为空，审计监听器沿用当前登录用户。
        var user = TokenUtil.getUserFromAuthToken();
        if (!fitsDatabase(user.getId(), 50) || !fitsDatabase(user.getUsername(), 50)) {
            throw new BusinessException("当前登录用户信息不符合合同审计字段要求，请联系管理员");
        }
        agreementRepository.saveAllAndFlush(entities);
        result.setSavedCount(entities.size());
        log.info("预登记合同批量导入成功，数量：{}", entities.size());
        return result;
    }

    /** 保留 Excel 物理行号和单位名称原文，名称不做 trim 或模糊转换。 */
    private List<ImportRow> readRows(MultipartFile file, PreAgreementImportResult result) {
        List<ImportRow> rows = new ArrayList<>();
        try (var input = file.getInputStream(); var workbook = new XSSFWorkbook(input)) {
            if (workbook.getNumberOfSheets() != 1) {
                throw new BusinessException("请使用导入模板，文件只能包含一个工作表");
            }
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter(Locale.CHINA);
            Row header = sheet.getRow(0);
            for (int c = 0; c < HEADERS.length; c++) {
                if (header == null || !HEADERS[c].equals(formatter.formatCellValue(header.getCell(c)))) {
                    error(result, 1, c, header == null ? "" : formatter.formatCellValue(header.getCell(c)),
                            "表头不正确，请使用模板中的“" + HEADERS[c] + "”列");
                }
            }
            if (!result.getErrors().isEmpty()) return rows;
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String[] values = new String[HEADERS.length];
                boolean empty = true;
                for (int c = 0; c < values.length; c++) {
                    Cell cell = row.getCell(c);
                    values[c] = formatter.formatCellValue(cell);
                    empty &= values[c].isEmpty();
                    if (cell != null && (cell.getCellType() == CellType.FORMULA || cell.getCellType() == CellType.ERROR)) {
                        error(result, row.getRowNum() + 1, c, values[c], "不允许公式或错误单元格，请填写文本值");
                    }
                }
                if (empty) continue;
                values[0] = values[0].trim().toUpperCase(Locale.ROOT);
                rows.add(new ImportRow(row.getRowNum() + 1, values));
                if (rows.size() > MAX_ROWS) throw new BusinessException("每次最多导入 1000 条合同");
            }
        } catch (IOException | org.apache.poi.ooxml.POIXMLException ex) {
            log.error("预登记合同 Excel 读取失败", ex);
            throw new BusinessException("无法读取 Excel，请使用未损坏、未加密的 .xlsx 模板文件");
        }
        if (rows.isEmpty() && result.getErrors().isEmpty()) {
            throw new BusinessException("Excel 中没有需要导入的合同数据");
        }
        result.setTotalCount(rows.size());
        return rows;
    }

    private Set<String> findExistingCodes(List<ImportRow> rows) {
        List<String> codes = rows.stream().map(r -> r.values[0]).filter(c -> fitsDatabase(c, 20)).distinct().toList();
        Set<String> existing = new HashSet<>();
        for (int start = 0; start < codes.size(); start += QUERY_BATCH_SIZE) {
            agreementRepository.findByAgreementCodeIn(codes.subList(start, Math.min(start + QUERY_BATCH_SIZE, codes.size())))
                    .forEach(a -> existing.add(codeKey(a.getAgreementCode())));
        }
        return existing;
    }

    private Map<String, List<CustomerInfo>> byName(List<CustomerInfo> customers) {
        return customers.stream().filter(c -> c.getCustomerName() != null)
                .collect(Collectors.groupingBy(CustomerInfo::getCustomerName));
    }

    private CustomerInfo match(PreAgreementImportResult result, ImportRow row, int column,
            Map<String, List<CustomerInfo>> choices) {
        String value = row.values[column];
        List<CustomerInfo> matches = choices.getOrDefault(value, Collections.emptyList());
        if (StringUtils.isBlank(value)) {
            error(result, row.number, column, value, "不能为空");
        } else if (matches.size() != 1) {
            error(result, row.number, column, value, matches.isEmpty()
                    ? "与创建功能中的可选单位名称不完全一致" : "存在同名客户，无法唯一确定单位，请联系管理员");
        } else if (!fitsDatabase(matches.get(0).getId(), 50)) {
            error(result, row.number, column, value, "单位档案 ID 缺失或不符合数据库字段要求");
        } else {
            return matches.get(0);
        }
        return null;
    }

    private void validateText(PreAgreementImportResult result, ImportRow row, int column, int maxBytes) {
        String value = row.values[column];
        if (StringUtils.isBlank(value)) {
            error(result, row.number, column, value, "不能为空");
        } else if (!fitsDatabase(value, maxBytes)) {
            error(result, row.number, column, value, "含数据库不支持的字符，或超过 varchar(" + maxBytes + ") 字节长度限制（中文通常占 2 字节）");
        }
    }

    private boolean fitsDatabase(String value, int maxBytes) {
        return !StringUtils.isBlank(value) && DATABASE_CHARSET.newEncoder().canEncode(value)
                && value.getBytes(DATABASE_CHARSET).length <= maxBytes;
    }

    private String codeKey(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFKC).trim().toUpperCase(Locale.ROOT);
    }

    private void error(PreAgreementImportResult result, int row, int column, String value, String message) {
        result.getErrors().add(new PreAgreementImportError(row, HEADERS[column], value, message));
    }

    @Override
    public void findTemplate(OutputStream output) throws IOException {
        try (var workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("预创建合同");
            Row header = sheet.createRow(0);
            CellStyle text = workbook.createCellStyle();
            text.setDataFormat(workbook.createDataFormat().getFormat("@"));
            for (int c = 0; c < HEADERS.length; c++) {
                header.createCell(c).setCellValue(HEADERS[c]);
                sheet.setDefaultColumnStyle(c, text);
                sheet.setColumnWidth(c, (c < 2 ? 30 : 48) * 256);
            }
            String[] companies = companyService.findYCompanyList().stream().map(CustomerInfo::getCustomerName).toArray(String[]::new);
            DataValidationHelper helper = sheet.getDataValidationHelper();
            DataValidation validation = helper.createValidation(helper.createExplicitListConstraint(companies),
                    new CellRangeAddressList(1, MAX_ROWS, 3, 3));
            validation.setShowErrorBox(true);
            validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            validation.createErrorBox("乙方单位错误", "请选择下拉列表中的完整公司名称");
            sheet.addValidationData(validation);
            sheet.createFreezePane(0, 1);
            workbook.write(output);
        }
    }

    private record ImportRow(int number, String[] values) { }
}
