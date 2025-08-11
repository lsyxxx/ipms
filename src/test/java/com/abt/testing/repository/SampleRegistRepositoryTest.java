package com.abt.testing.repository;

import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SampleRegistRepositoryTest {

    @Autowired
    private SampleRegistRepository sampleRegistRepository;

    @Test
    void findByEntrustIdAndCheckModuleId() {
        List<String> ids = List.of("AJC2025016Y013A", "AJC2025016Y013A");
        final Set<Tuple> list = sampleRegistRepository.findDistinctCheckModulesByEntrustId(new HashSet<>(ids));
        assertFalse(list.isEmpty());
        System.out.println(list.size());


    }
}