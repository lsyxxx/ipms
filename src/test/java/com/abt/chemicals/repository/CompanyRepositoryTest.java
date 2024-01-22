package com.abt.chemicals.repository;

import com.abt.chemicals.entity.Company;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void delete() {
        companyRepository.deleteById("123132");
    }

    @Test
    void find() {
        final List<Company> producer = companyRepository.findByTypeAndNameContainingOrderBySortAsc("producer", "", PageRequest.of(0, 50));
        producer.forEach(i -> {
            log.info(i.toString());
        });
    }

}