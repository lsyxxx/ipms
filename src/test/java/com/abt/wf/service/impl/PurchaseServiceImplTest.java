package com.abt.wf.service.impl;

import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.model.PurchaseApplyRequestForm;
import com.abt.wf.service.PurchaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PurchaseServiceImplTest {

    @Autowired
    private PurchaseService purchaseService;

    @Test
    void findAllByQueryPageable() {
        PurchaseApplyRequestForm requestForm = new PurchaseApplyRequestForm();
        requestForm.setPage(1);
        requestForm.setLimit(20);
        final Page<PurchaseApplyMain> page = purchaseService.findAllByQueryPageable(requestForm);
        Assertions.assertNotNull(page);
        System.out.println(page.getContent().size());
    }
}