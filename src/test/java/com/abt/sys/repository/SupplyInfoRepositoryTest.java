package com.abt.sys.repository;

import com.abt.sys.model.entity.SupplyInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SupplyInfoRepositoryTest {
    @Autowired
    private SupplyInfoRepository supplyInfoRepository;

    @Test
    void findDistinctBySupplierName() {
        final List<String> supplier = supplyInfoRepository.findDistinctActiveSupplierNames();
        assertNotNull(supplier);
        supplier.forEach(i -> System.out.println(i.toString()));
    }
}