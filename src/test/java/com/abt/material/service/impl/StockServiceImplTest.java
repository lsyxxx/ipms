package com.abt.material.service.impl;

import com.abt.material.entity.Stock;
import com.abt.material.entity.StockOrder;
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
        order.setStockLocation("西安首创");
        order.setOrderDate(LocalDate.now());
        order.setStockType(1);

        Stock s1 = new Stock();
        s1.setNum(1);
        s1.setPrice(new BigDecimal("266.00"));
        s1.setMaterialName("测试123");
        s1.setMaterialId("m111");
        List<Stock> stocks = new ArrayList<>();
        stocks.add(s1);

        order.setStockList(stocks);

        stockService.saveStockOrder(order);

    }

    @Test
    void findByOrderId() {
        final StockOrder order = stockService.findWithStockListByOrderId("175ddfd6-930f-4060-a0e8-52a286d8be50");
        assertNotNull(order);
        assertNotNull(order.getStockList());
        order.getStockList().forEach(s -> {
            System.out.printf("name: %s, num:%d", s.getMaterialName(), s.getNum());
        });
    }
}