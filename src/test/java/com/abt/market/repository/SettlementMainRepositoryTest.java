package com.abt.market.repository;

import com.abt.sys.model.entity.CustomerInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SettlementMainRepositoryTest {
    @Autowired
    private SettlementMainRepository settlementMainRepository;

    @Test
    void getAllCustomers() {
        final List<CustomerInfo> allCustomers = settlementMainRepository.getAllCustomers();
        assertNotNull(allCustomers);
        System.out.println(allCustomers.size());
        allCustomers.forEach(c -> System.out.printf("%s, %s\n", c.getCustomerid(), c.getCustomerName()));
    }
}