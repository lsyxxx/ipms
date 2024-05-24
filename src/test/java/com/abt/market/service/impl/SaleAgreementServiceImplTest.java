package com.abt.market.service.impl;

import com.abt.market.entity.SaleAgreement;
import com.abt.market.service.SaleAgreementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SaleAgreementServiceImplTest {

    @Autowired
    private SaleAgreementService saleAgreementService;


    @Test
    void marketBoardData() {
        final Map<String, Object> map = saleAgreementService.marketBoardData(2024);
        map.forEach((key, value) -> {
            System.out.println( key + ": " + value.toString());
        });

    }

    @Test
    void calculateContractAmount() {
        final List<SaleAgreement> list = saleAgreementService.findSaleAgreementCreatedByCurrentYear();
        System.out.println(list);
    }


}