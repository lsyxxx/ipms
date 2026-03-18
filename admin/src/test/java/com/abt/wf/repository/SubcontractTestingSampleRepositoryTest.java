package com.abt.wf.repository;

import com.abt.wf.entity.SubcontractTestingSample;
import com.abt.wf.entity.SubcontractTestingSettlementDetail;
import com.abt.wf.projection.SubcontractTestingSettlementDetailProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SubcontractTestingSampleRepositoryTest {
    @Autowired
    private SubcontractTestingSampleRepository subcontractTestingSampleRepository;

    @Test
    void findDetailsBy() {
//        final List<SubcontractTestingSettlementDetail> list = subcontractTestingSampleRepository.findDetailsBy("621faa40-f45c-4da8-9a8f-65b0c5353f40", "waisong111");
//        assertNotNull(list);
//        System.out.println(list.size());
    }

    @Test
    void testFindDetailsBy() {
        final List<SubcontractTestingSettlementDetailProjection> list = subcontractTestingSampleRepository.findDetailsBy("621faa40-f45c-4da8-9a8f-65b0c5353f40", "waisong111");
        assertNotNull(list);
        System.out.println(list.size());

    }
}