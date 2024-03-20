package com.abt.wf;

import com.abt.wf.model.ReimburseForm;
import com.abt.wf.repository.ReimburseTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

/**
 *
 */
@SpringBootTest
@Slf4j
public class ServiceTest {
    @Autowired
    private ReimburseTaskRepository reimburseTaskRepository;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void queryAll() {
        final List<ReimburseForm> list = reimburseTaskRepository
                .findReimburseWithCurrenTaskPageable(1, 40, "2024", "", null, null, null);
        Assert.notEmpty(list, "没查到");
        list.forEach(i -> {
            log.info("-- id: {}, currentTaskName: {}, assignee: {}", i.getId(), i.getCurrentTaskName(), i.getCurrentTaskAssigneeName());
        });
    }
}
