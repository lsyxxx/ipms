package com.abt.salary.service.impl;

import com.abt.salary.model.SalaryPreview;
import com.abt.salary.service.SalaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SalaryServiceImplTest {

    @Autowired
    private SalaryService salaryService;

    @Test
    void testPreview() {
//        final SalaryPreview salaryPreview = salaryService.extractAndValidate("C:\\Users\\Administrator\\Desktop\\salary_test.xlsx", "slm1");
    }
}