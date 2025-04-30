package com.abt.qrtzjob.service.impl;

import com.abt.qrtzjob.service.QuartzJobService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuartzJobServiceImplTest {

    @Autowired
    private QuartzJobService quartzJobService;

    @Test
    void createSalaryAutoConfirmJob() throws SchedulerException {
    }

    @Test
    void listPendingJobs() throws SchedulerException {
        final List<String> list = quartzJobService.listPendingJobs();
        Assertions.assertNotNull(list);
        System.out.println(list.size());
        list.forEach(System.out::println);
    }
}