package com.abt.material.repository;

import com.abt.material.entity.Stock;
import com.abt.material.entity.StockOrder;
import org.camunda.feel.syntaxtree.In;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockRepositoryTest {
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockOrderRepository stockOrderRepository;

    @Test
    void findByQueryPageable() {
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