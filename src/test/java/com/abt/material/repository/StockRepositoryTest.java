package com.abt.material.repository;

import com.abt.material.entity.Stock;
import com.abt.material.entity.StockOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockRepositoryTest {
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockOrderRepository stockOrderRepository;

    @Test
    void findByQueryPageable() {
        Pageable pageable = PageRequest.of(0, 10);
        final Page<Stock> page = stockRepository.findByQueryPageable("123", null, null, null, pageable);
        assertNotNull(page);
        System.out.println(page.getContent().size());
        page.getContent().forEach(System.out::println);
    }

    @Test
    void save() {
        StockOrder stockOrder = new StockOrder();
//        stockOrder.setStockType(1);
//        stockOrder.setOrderDate(LocalDate.now());
//        stockOrder.setStockLocation("西安");

        stockOrderRepository.save(stockOrder);
    }
}