package com.abt.oa.service.impl;

import com.abt.oa.entity.PaiBan;
import com.abt.oa.service.PaiBanService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaiBanServiceImplTest {

    @Autowired
    private PaiBanService paiBanService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findBetween() {
        final List<PaiBan> list = paiBanService.findBetween(LocalDate.of(2024, 6, 26), LocalDate.of(2024, 7, 25));
        assertNotNull(list);
        list.forEach(i ->  {
            System.out.printf("日期: %s, 类型: %s", i.getPaibandate().toString(), i.getPaibanType().toString());
        });
    }
}