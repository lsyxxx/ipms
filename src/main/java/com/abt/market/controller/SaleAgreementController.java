package com.abt.market.controller;

import com.abt.common.model.R;
import com.abt.market.entity.SaleAgreement;
import com.abt.market.model.SaleAgreementRequestForm;
import com.abt.market.service.SaleAgreementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
