package com.abt.salary.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SalarySlipRepositoryTest {
    @Autowired
    private SalarySlipRepository salarySlipRepository;

    @Test
    void countByIsSendAndMainId() {
        final int count = salarySlipRepository.countByIsSendAndMainId(true, "202406171718617591951");
        System.out.println(count);
    }
}