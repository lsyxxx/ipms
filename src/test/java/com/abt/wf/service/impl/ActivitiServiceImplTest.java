package com.abt.wf.service.impl;

import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.service.ActivitiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.abt.wf.config.WorkFlowConfig.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ActivitiServiceImplTest {
    @Autowired
    private ActivitiService activitiService;

    @Test
    void findFinanceTask() {

    }

    @Test
    void findUserTodoLatest1ByProcessDefinitionKeys() {

        final WorkflowBase load = activitiService.findUserTodoLatest1ByProcessDefinitionKeys("U20230406013"
                , financeWorkflowDef);
        assertNotNull(load);
        System.out.println(load);

    }

    @Test
    void countUserTodoByProcessDefinitionKeys() {
        final long count = activitiService.countUserFinanceTodo("U20230406013");
        System.out.println(count);
    }
}