package com.abt.material.service.impl;

import com.abt.material.entity.Stock;
import com.abt.material.entity.StockOrder;
import com.abt.material.model.StockSummaryTable;
import com.abt.material.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceImplTest {
    @Autowired
    private StockService stockService;

    @Test
    void saveStockOrder() {
        StockOrder order = new StockOrder();
        order.setStockType(1);


    }

    @Test
    void findByOrderId() {
    }

    @Test
    void inventoryGiftDetails() {
    }
}