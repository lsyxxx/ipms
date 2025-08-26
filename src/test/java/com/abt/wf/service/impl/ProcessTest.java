package com.abt.wf.service.impl;

import org.camunda.bpm.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@SpringBootTest
public class ProcessTest {
    @Autowired
    private RuntimeService runtimeService;


    @Test
    void testRestart() {

        Map<String, Object> vars = new HashMap<>();
        vars.put("restartedAt", System.currentTimeMillis());

        runtimeService.restartProcessInstances("rbsPay:31:4b856d7c-6226-11f0-ac2e-b4055dbeed78")
                .processInstanceIds("87073ebf-711b-11f0-84fc-b4055dbeed78")
                .startBeforeActivity("rbsPay_cashier")
                .initialSetOfVariables()
                .execute();
    }

}
