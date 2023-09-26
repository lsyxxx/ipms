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
        user.setId("test_id_apply");
        user.setName("lsy_apply");
        user.setAccount("abt_lsy_apply");


        //flowType
        FlowType flowType = new FlowType();
        flowType.setId("b93b47a7-56c5-11ee-ac20-a497b12f53fd");
        flowType.setCode("1672043402022");
        flowType.setName("日常报销（金额>=5000元）");
        flowType.setProcDefId(procDefId);

        applyForm = new ReimburseApplyForm(563.12, 11, new Date(), "报销事由：123");

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
}