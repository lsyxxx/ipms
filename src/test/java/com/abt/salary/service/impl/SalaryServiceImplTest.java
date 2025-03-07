package com.abt.salary.service.impl;

import com.abt.salary.AutoCheckSalaryJob;
import com.abt.qrtzjob.QuartzJobCreator;
import com.abt.salary.service.SalaryService;
import org.junit.jupiter.api.Test;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class SalaryServiceImplTest {

    @Autowired
    private SalaryService salaryService;
    @Autowired
    private AutoCheckSalaryJob autoCheckSalaryJob;
    @Autowired
    private QuartzJobCreator quartzJobCreator;

    @Test
    void testPreview() {
//        final SalaryPreview salaryPreview = salaryService.extractAndValidate("C:\\Users\\Administrator\\Desktop\\salary_test.xlsx", "slm1");
    }

    @Test
    void testFind() {
    }

    @Test
    void testJob() throws SchedulerException {
    }
}