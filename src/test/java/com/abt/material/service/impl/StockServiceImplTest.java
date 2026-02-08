package com.abt.material.service.impl;

import com.abt.material.entity.Inventory;
import com.abt.material.entity.MaterialType;
import com.abt.material.entity.Stock;
import com.abt.material.entity.StockOrder;
import com.abt.material.model.IStockTimelineDTO;
import com.abt.material.model.InventoryRequestForm;
import com.abt.material.model.StockSummaryTable;
import com.abt.material.service.StockService;
import com.abt.sys.util.WithQueryUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Test
    void inventoryExcel() throws Exception {

        stockService.createGiftInventoryAndValueExcel(null, 2024, 2025, List.of(1, 2, 3, 4, 5, 6));

    }

    @Test
    void testInventory() {
        InventoryRequestForm form = new InventoryRequestForm();
        form.setMaterialTypeIds(List.of("1859b876-fe05-4f4c-9a77-148137dd23a5", "df69857b-4843-465c-b41b-5e8d04b3a94b", "667018fe-70e4-4b74-9d79-0de6385a88c2"));
        form.setLimit(9999);
        final List<Inventory> inventoryList = stockService.latestInventories(form).getContent();
        WithQueryUtil.build(inventoryList);
        Assertions.assertNotNull(inventoryList);
        inventoryList.forEach(i -> {
            System.out.printf("%s-%s[%s]: %.2f, %.2f\n", i.getMaterialType(), i.getMaterialName(), i.getWarehouseName(), i.getQuantity(), i.getUnitPrice());
        });
    }

    @Test
    void testPurchaseSummary() {

    }

    @Test
    void testRecords() {
        String materialId = "e144f5a4-4463-4965-af5c-8ba71bac3596";
        String whid = "44746bb0-30dd-48ad-a57d-de1e160bf1d4";
        LocalDateTime startDate = LocalDate.of(2025,1,1).atStartOfDay();
        LocalDateTime endDate = LocalDateTime.now();

        final List<IStockTimelineDTO> list = stockService.getStockHistory(materialId, whid, startDate, endDate);
        System.out.println(list.size());
    }
}