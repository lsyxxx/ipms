package com.abt.flow.service.impl;

import com.abt.flow.model.entity.BizFlowCategory;
import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.flow.service.FlowInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
@Slf4j
class FlowInfoServiceImplTest {

    @Autowired
    private FlowInfoService service;
    @Autowired
    private Example<BizFlowCategory>  enableExample;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUserApplyFlows() {

    }

    @Test
    void findAllEnabled() {
        List<BizFlowCategory> allEnabled = service.findAllEnabled(0, 10);
        Assert.notEmpty(allEnabled, "list is empty");
        allEnabled.forEach(i -> {
            log.info(i.toString());
        });
    }


    String userId = "abt019";
    int page = 0;
    int size = 20;
    String type = "";
    String query = "补卡";




}