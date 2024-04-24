package com.abt.testing.repository;

import com.abt.testing.entity.Agreement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class AgreementRepositoryTest {

    @Autowired
    private AgreementRepository agreementRepository;

    @Test
    void deleteInBatch() {
    }

    @Test
    void existsByAgreementCode() {
        final boolean check = agreementRepository.existsByAgreementCode("AJC2024065");
        assertFalse(check);

    }

    @Test
    void load() {
        agreementRepository.findById("0155a374-1b53-4636-864f-550fdd22eda0")
                .ifPresent(i -> {
                    assertNotNull(i.getYCompany());
                    System.out.println(i.getYCompany().toString());
                });

    }
}