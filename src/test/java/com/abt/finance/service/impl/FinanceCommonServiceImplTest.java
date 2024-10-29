package com.abt.finance.service.impl;

import com.abt.finance.service.FinanceCommonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FinanceCommonServiceImplTest {
    @Autowired
    private FinanceCommonService financeCommonService;

    @Test
    void createAllAccountItemCascade() {

        financeCommonService.createAllAccountItemCascade();
    }
}