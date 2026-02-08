package com.abt.market.controller;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.R;
import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.SaleAgreementRequestForm;
import com.abt.market.model.SettlementAgreementDTO;
import com.abt.market.service.SaleAgreementService;
import com.abt.market.service.SettlementService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.SystemFile;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.service.InvoiceApplyService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/market/sale")
public class SaleAgreementController {

    private final SaleAgreementService saleAgreementService;
    private final InvoiceApplyService invoiceApplyService;
    private final SettlementService settlementService;

    public SaleAgreementController(SaleAgreementService saleAgreementService, InvoiceApplyService invoiceApplyService, SettlementService settlementService) {
        this.saleAgreementService = saleAgreementService;
        this.invoiceApplyService = invoiceApplyService;
        this.settlementService = settlementService;
    }

    @GetMapping("/query")
    public R<List<SaleAgreement>> findSaleAgreementsByQuery(SaleAgreementRequestForm requestForm) {
        final Page<SaleAgreement> paged = saleAgreementService.findByQuery(requestForm);
        return R.success(paged.getContent(), (int) paged.getTotalElements());
    }

    @GetMapping("/find")
    public R<List<SaleAgreement>> findSaleAgreements(SaleAgreementRequestForm requestForm) {
        final Page<SaleAgreement> paged = saleAgreementService.findPaged(requestForm);
        return R.success(paged.getContent(), (int) paged.getTotalElements());
    }


    @GetMapping("/del")
    public R<Object> delete(String id) {
        saleAgreementService.delete(id);
        return R.success("删除成功");
    }

    @GetMapping("/finda")
    public R<List<String>> findAllPartyA() {
        final List<String> allPartyA = saleAgreementService.findAllPartyA();
        return R.success(allPartyA);
    }

    @GetMapping("/find/curweek")
    public R<List<SaleAgreement>> findByCurrentWeek() {
        final List<SaleAgreement> list = saleAgreementService.findSaleAgreementCreatedByCurrentWeek();
        return R.success(list);
    }

    @GetMapping("/find/curmon")
    public R<List<SaleAgreement>> findByCurrentMonth() {
        final List<SaleAgreement> list = saleAgreementService.findSaleAgreementCreatedByCurrentMonth();
        return R.success(list);
    }

    @GetMapping("/find/curyear")
    public R<List<SaleAgreement>> findByCurrentYeaar() {
        final List<SaleAgreement> list = saleAgreementService.findSaleAgreementCreatedByCurrentYear();
        return R.success(list);
    }

    @GetMapping("/count")
    public R<Long> countAll() {
        final long count = saleAgreementService.countAllSaleAgreement();
        return R.success(count);
    }

    @PostMapping("/save")
    public R<Long> save(@Validated(ValidateGroup.Save.class) @RequestBody SaleAgreement saleAgreement) {
        saleAgreementService.saveSaleAgreement(saleAgreement);
        return R.success("保存成功");
    }

    @GetMapping("/load")
    public R<SaleAgreement> load(String id) {
        final SaleAgreement saleAgreement = saleAgreementService.LoadSaleAgreement(id);
        return R.success(saleAgreement);
    }

    @GetMapping("/board")
    public R<Map<String, Object>> board() {
        final Map<String, Object> map = saleAgreementService.marketBoardData(LocalDate.now().getYear());
        return R.success(map);
    }


    @GetMapping("/export")
    public void export(SaleAgreementRequestForm requestForm, HttpServletResponse response) {
        try {
            //不分页
            requestForm.setLimit(99999);
            requestForm.setPage(1);

            final Page<SaleAgreement> page = saleAgreementService.findByQuery(requestForm);
            final List<SaleAgreement> list = page.getContent();

            for (SaleAgreement saleAgreement : list) {
                saleAgreement.format();
                //是否含税
                saleAgreement.setIncludeTaxStr(saleAgreement.translateBoolean(saleAgreement.isIncludeTax()));
                String code = saleAgreement.getCode();
                //开票数量
                final List<InvoiceApply> invoices = invoiceApplyService.findSaleAgreementInvoices(code);
                saleAgreement.setInvoiceCount(invoices.size());
                final double invoiceTotal = invoices.stream().mapToDouble(InvoiceApply::getInvoiceAmount).sum();
                saleAgreement.setInvoiceTotal(BigDecimal.valueOf(invoiceTotal).setScale(2, RoundingMode.HALF_UP));
                final String invStr = invoices.stream().map(InvoiceApply::getId).collect(Collectors.joining(","));
                saleAgreement.setInvoiceIds(invStr);
                //结算单
                final List<SettlementAgreementDTO> stlms = settlementService.findSettlementsByContractNo(code);
                //结算总金额
//            saleAgreement.setSettlementCount(stlms.size());
//            final BigDecimal stlmTotal = stlms.stream().map(SettlementAgreementDTO::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
//            saleAgreement.setSettlementTotal(stlmTotal);
//            final String stlmIds = stlms.stream().map(SettlementAgreementDTO::getSettlementId).collect(Collectors.joining(","));
//            saleAgreement.setSettlementIds(stlmIds);
                // 附件
                final List<SystemFile> systemFiles = saleAgreement.convertSystemFiles();
                final String fileNames = systemFiles.stream().map(SystemFile::getName).collect(Collectors.joining(","));
                saleAgreement.setFileNames(fileNames);
            }

            // 构建文件名
            String fileName = "销售合同.xlsx";

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"");
            saleAgreementService.exportSaleAgreementList(list, response.getOutputStream());
        } catch (Exception e) {
            log.error("导出销售合同失败", e);
            throw new BusinessException("生成销售合同Excel失败！");
        }
    }


}
