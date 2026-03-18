package com.abt.testing.service.impl;

import com.abt.testing.entity.SampleRegistCheckModeuleItem;
import com.abt.testing.service.SampleRegistCheckModeuleItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SampleRegistCheckModeuleItemServiceImplTest {
    @Autowired
    private SampleRegistCheckModeuleItemService sampleRegistCheckModeuleItemService;

    @Test
    void findByEntrustIds() {
        Sort sort = Sort.by(Sort.Direction.ASC, "entrustId", "sampleRegistId", "checkModeuleId");
        final List<SampleRegistCheckModeuleItem> list = sampleRegistCheckModeuleItemService.findByEntrustIds(List.of("AJC2025016Y006A", "AJC2024012Y010"), sort);
        Assertions.assertNotNull(list);
        System.out.println("size: " + list.size());


    }
}