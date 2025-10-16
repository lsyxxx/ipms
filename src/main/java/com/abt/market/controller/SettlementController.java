package com.abt.market.controller;

import cn.idev.excel.FastExcel;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.market.entity.SettlementMain;
import com.abt.market.model.*;
import com.abt.market.service.SettlementService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.CustomerInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 结算
 */
@RestController
@Slf4j
@RequestMapping("/stlm")
public class SettlementController {
    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping("/save/draft")
    public R<Object> saveDraft(@RequestBody SettlementMain settlementMain) {
        settlementMain.setSaveType(SaveType.TEMP);
        settlementService.save(settlementMain);
        return R.success("草稿保存成功");
    }

    @PostMapping("/save")
    public R<Object> save(@Validated({ValidateGroup.Save.class}) @RequestBody SettlementMain settlementMain) {
        settlementMain.setSaveType(SaveType.SAVE);
        settlementService.save(settlementMain);
        return R.success("保存成功");
    }

    @GetMapping("/find/page")
    public R<Page<SettlementMainListDTO>> findByQuery(@ModelAttribute SettlementRequestForm requestForm) {
        Pageable pageable = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(), Sort.by(Sort.Order.desc("createDate")));
        final Page<SettlementMainListDTO> page = settlementService.findMainOnlyByQuery(requestForm, pageable);
        return R.success(page);
    }

    @GetMapping("/detail/{id}")
    public R<SettlementMain> loadEntity(@PathVariable String id) {
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("结算单id未传入");
        }
        final SettlementMain entity = settlementService.findSettlementMainWithAllItems(id);
        return R.success(entity, "加载成功");
    }

    @GetMapping("/delete")
    public R<Object> delete(String id) {
        final SettlementMain main = settlementService.findSettlementMainOnly(id);
        if (SaveType.TEMP == main.getSaveType()) {
            settlementService.delete(id);
        } else {
            throw new BusinessException("只能删除暂存的结算单!");
        }
        return R.success("删除成功");
    }

    /**
     * 获取所有结算客户
     */
    @GetMapping("/clients")
    public R<List<CustomerInfo>> getClients() {
        final List<CustomerInfo> clients = settlementService.getClients();
        return R.success(clients);
    }

    @GetMapping("/detail/export")
    public void exportAndDownload(String id, HttpServletResponse response) {
        try {
            // 获取结算单数据
            SettlementMain settlementMain = settlementService.findSettlementMainWithAllItems(id);
            
            // 构建文件名
            String fileName = "结算单_" + settlementMain.getClientName() + "_" + id + ".xlsx";
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"");
            
            settlementService.createSettlementExcel(settlementMain, response.getOutputStream());
            
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("下载结算单失败", e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 结算单作废
     */
    @GetMapping("/invalid")
    public R<Object> invalid(String id, String reason) {
        final SettlementMain main = settlementService.findSettlementMainOnly(id);
        main.setInvalidReason(reason);
        settlementService.invalid(main);
        return R.success("作废成功");
    }

    /**
     * 保存关联
     */
    @PostMapping("/rel/update")
    public R<Object> saveRelations(@RequestBody RelationRequest relationRequest) {
        settlementService.updateRelations(relationRequest);
        return R.success("更新成功!");
    }

    /**
     * 按样品导入
     */
    @PostMapping("/import/bysample")
    public R<Object> importBySamples(@RequestParam("file") MultipartFile file) throws IOException {
        List<ImportSample> list = FastExcel.read(file.getInputStream())
                .head(ImportSample.class)
                .sheet()  // 默认读取第一个sheet
                .doReadSync();
        return R.success("共读取" + list.size() + "条数据");
    }
}
