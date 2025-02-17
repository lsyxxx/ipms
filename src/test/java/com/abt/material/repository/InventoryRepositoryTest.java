package com.abt.material.repository;

import com.abt.material.entity.Inventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InventoryRepositoryTest {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    void findAllLatestInventory() {

    }
}