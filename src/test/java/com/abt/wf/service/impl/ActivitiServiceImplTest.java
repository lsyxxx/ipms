package com.abt.wf.service.impl;

import com.abt.wf.service.ActivitiService;
import com.abt.wf.service.PayVoucherService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.runtime.RestartProcessInstanceBuilder;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PayVoucherService payVoucherService;



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

    @Test
    void findUserTaskDef() {
        final List<Task> tasks = taskService.createNativeTaskQuery()
                .sql("select distinct(NAME_) from ACT_RU_TASK where ASSIGNEE_ = #{userid} and PROC_DEF_ID_ LIKE #{procDefIdLike}")
                .parameter("userid", "U20230406007")
                .parameter("procDefIdLike", "%rbsMulti%")
                .list();
        Assert.notEmpty(tasks, "task 空");
        tasks.forEach(t -> {
            System.out.println(t.getName());
        });
    }

    @Test
    void findUserTaskName() {
        final Query query = entityManager.createNativeQuery("select distinct(NAME_) from ACT_RU_TASK where ASSIGNEE_ = :userid and PROC_DEF_ID_ LIKE :procDefIdLike");
        query.setParameter("userid", "U20230406007");
        query.setParameter("procDefIdLike", "%rbsMulti%");
        final List resultList = query.getResultList();
        Assert.notEmpty(resultList, "result list is null");
        resultList.forEach(System.out::println);


    }

    @Test
    void restart() {

        Map<String, Object> variables = new HashMap<>();
        variables.put("restart", true);

        runtimeService.restartProcessInstances("rbsPay:19:2c4041e2-ad67-11ef-b541-522f9b379759")
                .processInstanceIds("f8c39d4d-b5d7-11ef-b615-522f9b379759")
                .startBeforeActivity("rbsPay_cashier")
                .execute();
    }

    @Test
    void findFinished() {
        final List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().finished().list();
        Assert.notEmpty(list, "not empty");
        list.forEach(i -> {
            System.out.printf("procId: %s, def: %s\n", i.getRootProcessInstanceId(), i.getProcessDefinitionKey());
        });
    }

    @Test
    void findPay() {
        final List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processDefinitionId("rbsPay:19:2c4041e2-ad67-11ef-b541-522f9b379759").active()
                .list();
        Assert.notEmpty(list, "not empty");
        list.forEach(System.out::println);
    }

    @Test
    void findHistory() {
        final List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
                .superProcessInstanceId("f8c39d4d-b5d7-11ef-b615-522f9b379759").active()
                .orderByProcessInstanceStartTime().desc().list();
        Assert.notEmpty(list, "not empty");
        list.forEach(System.out::println);
    }
}