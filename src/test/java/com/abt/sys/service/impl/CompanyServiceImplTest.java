package com.abt.sys.service.impl;

import com.abt.sys.model.entity.SupplyInfo;
import com.abt.sys.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CompanyServiceImplTest {

    @Autowired
    private CompanyService companyService;

    @Test
    void findAllSupplier() {
        final List<String> all = companyService.findAllSupplier();
        assertNotNull(all);
        all.forEach(i -> System.out.println(i.toString()));
    }
}