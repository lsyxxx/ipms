package com.abt.market.service.impl;

import com.abt.market.entity.SettlementSummary;
import com.abt.market.service.SettlementEntrustService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SettlementEntrustServiceImplTest {

    @Autowired
    private SettlementEntrustService settlementEntrustService;

    @Test
    void findSettlementEntrust() {
    }

    @Test
    void checkModuleSettlement() {
        final List<SettlementSummary> summaries = settlementEntrustService.checkModuleSettlement("JC2024005");
        Assertions.assertNotNull(summaries);
        System.out.println(summaries.size());
        summaries.forEach(summary -> {
            System.out.printf("%s|%s|%d|%.2f%n", summary.getEntrustId(), summary.getCheckModuleName(), summary.getSampleNum(), summary.getAmount());
        });
    }
}