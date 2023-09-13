package com.abt.flow.service.impl;

import com.abt.common.model.RequestForm;
import com.abt.flow.model.entity.FlowCategory;
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
class FlowInfoServiceImplTest extends BaseTest{

    @Autowired
    private FlowInfoService service;
    @Autowired
    private Example<FlowCategory>  enableExample;

    private RequestForm form;

    @BeforeEach
    void setUp() {
        form = new RequestForm();
        form.setLimit(20);
        form.setPage(0);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getUserApplyFlows() {

    }

    @Test
    void findAllEnabled() {
        List<FlowCategory> allEnabled = service.findAllEnabled(0, 10);
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

    @Test
    void getTodoFlows() {
        form.setId("abt010");

        List<BizFlowRelation> todoFlows = service.getTodoFlows(form);
        logListElement(todoFlows);
    }

    @Test
    void getCompletedFlows() {


    }

    @Test
    void getFlows() {
    }
}