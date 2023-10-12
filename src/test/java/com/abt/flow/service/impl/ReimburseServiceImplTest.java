package com.abt.flow.service.impl;

import com.abt.common.model.User;
import com.abt.common.util.JsonUtil;
import com.abt.flow.model.FlowType;
import com.abt.flow.model.ReimburseApplyForm;
import com.abt.flow.service.ReimburseService;
import com.abt.sys.model.dto.UserView;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
class ReimburseServiceImplTest extends BaseTest{

    @Autowired
    private ReimburseService service;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private Map<String, User> defaultAuditor;

    UserView user = new UserView();

    ReimburseApplyForm applyForm;

    private String procDefId;

    ProcessDefinition rbsDef;

    @BeforeEach
    void setUp() throws JsonProcessingException {

        rbsDef  = latestProcDefByName("Normal reimburse less 5000 process");
        procDefId = rbsDef.getId();

        //userView
        user.setId("45af5ac3-9c89-4244-9f8d-ddc056b0e7b1");
        user.setName("阿伯塔管理员");
        user.setAccount("abtadmin");


        //flowType
        FlowType flowType = new FlowType();
        flowType.setId("9c88ae57-373a-4bcd-915f-dddc15803aba");
        flowType.setCode("1696907356572");
        flowType.setName("流程申请测试勿用");
        flowType.setProcDefId(procDefId);

        applyForm = new ReimburseApplyForm(563.12, 11, new Date(), "报销事由：测试测试abtadmin");

        //applyForm
        applyForm.setFlowType(flowType);
        applyForm.setDescription("车旅费");

        String json = JsonUtil.toJson(applyForm);
//        log.info("===== json: {}", json);

    }

    ProcessDefinition latestProcDefByName(String name) {
        ProcessDefinition def = repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionName(name).singleResult();
        log.info("---最新的 {} 流程定义: id: {}, version: {}", name, def.getId(), def.getVersion());
        return def;
    }

    @Test
    void apply() {
        service.apply(user, applyForm);
    }

    @Test
    void departmentAudit() {
        applyForm.setProcessInstanceId("18649181-56b4-11ee-bb3f-a497b12f53fd");
        applyForm.setDecision("Reject");
        service.departmentAudit(user, applyForm);
    }

    @Test
    void techLeadAudit() {
    }

    @Test
    void ceoAudit() {
    }

    @Test
    void accountantAudit() {
    }

    @Test
    void financeManagerAudit() {
    }

    @Test
    void taxOfficerAudit() {
    }

    @Test
    void testAuditorMap() {
        System.out.println(this.defaultAuditor);
    }



    @Test
    void printTask() {
//        Task activeTask = taskService.createTaskQuery()
//                .active()
////                .processInstanceBusinessKey("202309191695116476706")
//                .processInstanceId("ae051929-56d0-11ee-8bf3-a497b12f53fd")
//                .singleResult();
//        Assert.notNull(activeTask, "Task 不存在！");
//        logTask(activeTask);
//        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
//                .orderByTaskCreateTime().desc()
//                .list();
//        countList(list);
//        list.forEach(i -> {
//            logTask(i);
//        });

        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .startedBy("test_id_apply")
                .active()
                .orderByStartTime().desc()
                .list();

        countList(list);
        list.forEach(i -> {
            logProcess(i);
        });

//        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
//                .processInstanceId("18649181-56b4-11ee-bb3f-a497b12f53fd")
//                .orderByTaskCreateTime().desc()
//                .list();
//        countList(list);
//        list.forEach(i -> {
//            logTask(i);
//        });
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void get() {
        String rbsId = "202309191695111409784";
//        service.get();
    }


    void doPrintProcess(String procId) {
        final ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procId).singleResult();
        logProcess(processInstance);
    }


    void doPrintTasks(String procId) {
        final List<Task> tasks = taskService.createTaskQuery().processInstanceId(procId).list();
        countList(tasks);
        for (Task task : tasks) {
            logTask(task);
        }
    }


    @Test
    void printTasks() {
        String procId = "722c29ad-6816-11ee-a16b-a497b12f53fd";
        doPrintTasks(procId);
    }


    @Test
    void printVars() {
        String procId = "722c29ad-6816-11ee-a16b-a497b12f53fd";
        final List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(procId).list();
        countList(list);
        list.forEach(i -> {
            log.info("---his vars: value: {}, name: {}", i.getValue(), i.getVariableName());
        });
    }
}