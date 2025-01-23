package com.abt.testing.service.impl;

import com.abt.market.entity.SettlementItem;
import com.abt.testing.entity.Entrust;
import com.abt.testing.model.EntrustRequestForm;
import com.abt.testing.service.EntrustService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EntrustServiceImplTest {
    @Autowired
    private EntrustService entrustService;

    @Test
    void findByQueryPageable() {
        EntrustRequestForm requestForm  = new EntrustRequestForm();
        requestForm.setQuery("JC2024003B");
        final List<Entrust> list = entrustService.findEntrustListByQuery(requestForm);
        assertNotNull(list);
        list.forEach(e -> {
            System.out.printf("检测编号:%s, 客户id: %s, 客户名称: %s, 项目:%s\n",
                    e.getId(), e.getCustomNo(), e.getCustomer().getCustomerName(), e.getProjectName());
        });
    }


    @Test
    void findSampleCheckModules() {
        final List<SettlementItem> list = entrustService.findSampleCheckModules("AJC2024013Y010");
        assertNotNull(list);
        list.forEach(e -> {
            System.out.printf("检测项目: %s, 样品数量: %s\n", e.getModuleName(), e.getSampleNum());
        });
    }
}