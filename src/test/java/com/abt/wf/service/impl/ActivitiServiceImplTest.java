package com.abt.wf.service.impl;

import com.abt.wf.service.ActivitiService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivitiServiceImplTest {

    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    void testCompleted() {
        final ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().active().processInstanceId("").singleResult();

    }

    void taskCompleted() {
        taskService.complete(taskService.createTaskQuery().active().processInstanceId("").singleResult().getId());
    }

    void deleteProcessInstance(String processInstanceId, String deleteReason) {
        runtimeService.deleteProcessInstance(processInstanceId, deleteReason);
    }

    @Test
    void doTest() {
//        deleteProcessInstance("0bd1cf78-3cf8-11ef-a167-b4055dbeed78", "系统删除");
//        deleteProcessInstance("16cc8206-3dde-11ef-9fc0-b4055dbeed78", "系统删除");
//        deleteProcessInstance("805e619c-3cde-11ef-a167-b4055dbeed78", "系统删除");
    }
}