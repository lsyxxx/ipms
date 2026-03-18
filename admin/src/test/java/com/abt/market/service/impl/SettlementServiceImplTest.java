package com.abt.market.service.impl;

import com.abt.market.entity.SettlementMain;
import com.abt.market.model.SettlementAgreementDTO;
import com.abt.market.service.SettlementService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class SettlementServiceImplTest {
    @Autowired
    private SettlementService settlementService;

    @Test
    void save() {
    }

    @Test
    void findSettlementsByContractNo() {
        final List<SettlementAgreementDTO> list = settlementService.findSettlementsByContractNo("ABT2025HT027");
        Assertions.assertNotNull(list);

        System.out.println(list.size());

        list.forEach(i -> {
            System.out.println(i.toString());
        });

    }
}