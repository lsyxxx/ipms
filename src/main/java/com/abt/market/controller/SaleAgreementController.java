package com.abt.market.controller;

import com.abt.common.model.R;
import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.SaleAgreementRequestForm;
import com.abt.market.service.SaleAgreementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/market/sale")
public class SaleAgreementController {

    private final SaleAgreementService saleAgreementService;

    public SaleAgreementController(SaleAgreementService saleAgreementService) {
        this.saleAgreementService = saleAgreementService;
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

    @GetMapping("/count")
    public R<Long> countAll() {
        final long count = saleAgreementService.countAllSaleAgreement();
        return R.success(count);
    }

    @PostMapping("/save")
    public R<Long> save(SaleAgreement saleAgreement) {
        saleAgreementService.saveSaleAgreement(saleAgreement);
        return R.success("保存成功");
    }

    @GetMapping("/load")
    public R<SaleAgreement> load(String id) {
        final SaleAgreement saleAgreement = saleAgreementService.LoadSaleAgreement(id);
        return R.success(saleAgreement);
    }


}
