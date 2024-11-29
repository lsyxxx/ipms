package com.abt.liaohe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TestBaseTableRepositoryTest {

    @Autowired
    private TestBaseTableRepository testBaseTableRepository;

    @Test
    void findByReportName() {
        final List<TestBaseTable> all = testBaseTableRepository.findAll();
        System.out.println(all.size());

    }
}