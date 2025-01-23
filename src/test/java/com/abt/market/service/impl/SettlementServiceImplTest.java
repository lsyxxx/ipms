package com.abt.market.service.impl;

import com.abt.market.entity.SettlementItem;
import com.abt.market.entity.SettlementMain;
import com.abt.market.service.SettlementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class SettlementServiceImplTest {
    @Autowired
    private SettlementService settlementService;

    @Test
    void save() {
        SettlementMain main = new SettlementMain();
        main.setCode("JS-001");
        main.setClientName("辽河");
        main.setCompanyName("阿伯塔");
        main.setSum(3789.56);

        List<SettlementItem> list = new ArrayList<>();
        SettlementItem i1 = new SettlementItem();
        i1.setEntrustNo("AJC2024001H001");
        i1.setModuleName("孔隙度");
        i1.setPrice(56.00);
        i1.setSampleNum(100);
        i1.setSum(5600.00);
        list.add(i1);

        SettlementItem i2 = new SettlementItem();
        i2.setEntrustNo("AJC2024001H002");
        i2.setModuleName("渗透率");
        i2.setPrice(10.00);
        i2.setSampleNum(10);
        i2.setSum(100.00);
        list.add(i2);
        main.setSettlementItems(list);
        settlementService.save(main);
    }
}