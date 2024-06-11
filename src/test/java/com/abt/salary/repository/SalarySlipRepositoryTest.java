package com.abt.salary.repository;

import com.abt.salary.entity.SalarySlip;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SalarySlipRepositoryTest {
    @Autowired
    private SalarySlipRepository salarySlipRepository;

    @Test
    void findByMainIdAndErrorIsNotEmpty() {
        final List<SalarySlip> list = salarySlipRepository.findByMainIdAndErrorNotEmpty("");

    }

    @Test
    void findByMainId() {
    }

    @Test
    void findByMainIdAndSend() {
        final List<SalarySlip> list = salarySlipRepository.findByMainIdAndIsSend("", true);
    }

    @Test
    void countByIsSendAndMainId() {
        final int i = salarySlipRepository.countByMainIdAndIsSend("", true);
    }

    @Test
    void countByIsCheckAndMainId() {
        final int i = salarySlipRepository.countByMainIdAndIsCheck("", false);
    }

    @Test
    void countByIsReadAndMainId() {
        final int i = salarySlipRepository.countByMainIdAndIsRead("", false);
    }
}