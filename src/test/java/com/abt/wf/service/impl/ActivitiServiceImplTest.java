package com.abt.wf.service.impl;

import com.abt.wf.service.ActivitiService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivitiServiceImplTest {

    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;



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


    @Test
    void testDelete() {
        taskService.deleteTask("");

        final List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee("b6883628-ab11-4ad8-a533-11632fcdb4f3").finished().list();
        assertNotNull(list);
        list.forEach(i -> {
            if (i.getProcessInstanceId().equals("ef25803d-48fe-11ef-ad7c-b4055dbeed78")) {
                System.out.println("--FOUND---");
            }
            System.out.printf("procInst: %s, endTime: %s \n", i.getProcessInstanceId(), i.getEndTime().toString());
        });
    }


    @Test
    void deleteTask() {
//        final HistoricTaskInstance runningTask = historyService.createHistoricTaskInstanceQuery().processInstanceId("a108002e-5b72-11ef-af73-522f9b379759").unfinished().singleResult();
//        System.out.println(runningTask.getId());
//        historyService.deleteHistoricTaskInstance(runningTask.getId());

        runtimeService.deleteProcessInstance("a108002e-5b72-11ef-af73-522f9b379759", "先删除historyTask");
    }
}