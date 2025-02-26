package com.abt.material.repository;

import com.abt.material.entity.Inventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InventoryRepositoryTest {
    @Autowired
    private InventoryRepository inventoryRepository;


    @Test
    void findLatestInventory() {
        Pageable pageable = PageRequest.of(0, 999);
        final Page<Inventory> page = inventoryRepository.findLatestInventory(List.of(), List.of(), null, pageable);
        System.out.println(page.getTotalElements());
        page.getContent().forEach(i -> {
            System.out.printf("name：%s, num: %d, alert: %d \n", i.getMaterialName(), i.getQuantity(), i.getAlert() == null ? -1 : i.getAlert().getAlertNum());
        });
    }
}