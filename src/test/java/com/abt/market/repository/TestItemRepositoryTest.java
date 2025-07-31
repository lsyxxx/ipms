package com.abt.market.repository;

import com.abt.market.entity.TestItem;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TestItemRepositoryTest {
    @Autowired
    private TestItemRepository testItemRepository;

    @Test
    void findUnsettledSamples() {

        final List<Tuple> list = testItemRepository.findUnsettledSamples(Set.of("AJC2025046Y014A"));
        Assertions.assertNotNull(list);
        list.forEach(item -> {
            System.out.printf("sampleNo：%s, checkModuleName: %s \n", item.get("sample_no"), item.get("check_module_name"));
        });
    }
}