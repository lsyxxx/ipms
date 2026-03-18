package com.abt.material.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockOrderRepositoryTest {
    @Autowired
    private StockOrderRepository stockOrderRepository;

    @Test
    void findWithDetails() {
        stockOrderRepository.findByIdWithAllDetail("");
    }
}