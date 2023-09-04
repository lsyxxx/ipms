package com.abt.flow.service.impl;

import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.repository.FlowOperationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class FlowOperationLogServiceImplTest {

    private FlowOperationLogServiceImpl service;

    @Autowired
    private FlowOperationLogRepository repository;

    @BeforeEach
    void setUp() {
        service = new FlowOperationLogServiceImpl(repository);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void insertOne() {
    }


    @Test
    void createData() {

    }
    @Test
    void getByProcessInstanceId() {
    }

    @Test
    void getAllOrderByOperateDate() {
        List<FlowOperationLog> list = service.getAllOrderByOperateDate(0, 10);
        Assert.notEmpty(list, "-------- list is empty");
        list.forEach(l -> {
            System.out.println(l.toString());
        });
    }
}