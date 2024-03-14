package com.abt.wfbak.service.impl;

import com.abt.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.camunda.bpm.engine.ProcessEngineConfiguration.HISTORY_FULL;


@Slf4j
class WorkFlowServiceImplTest {

    private ProcessEngine processEngine;
    private RuntimeService runtimeService;
    private TaskService taskService;
    private HistoryService historyService;
    private FilterService filterService;
    private RepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        this.processEngine = init();
        this.runtimeService = processEngine.getRuntimeService();
        this.taskService = processEngine.getTaskService();
        this.historyService = processEngine.getHistoryService();
        this.filterService = processEngine.getFilterService();
        this.repositoryService = processEngine.getRepositoryService();
    }

    ProcessEngine init2() {
        return ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .setJdbcUrl("jdbc:mysql://localhost:3306/camunda?useUnicode=true&character_set_server=utf8mb4&allowMutiQueries=true")
                .setJdbcDriver("com.mysql.cj.jdbc.Driver")
                .setJdbcUsername("root")
                .setJdbcPassword("123456")
                .setJobExecutorActivate(false)
                .setHistory(HISTORY_FULL)
                .setJobExecutorActivate(false)
                .buildProcessEngine();
    }

    ProcessEngine init() {
        return ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
                .setJdbcUrl("jdbc:sqlserver://localhost:1433;database=abt_biz;trustServerCertificate=true")
                .setJdbcDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                .setJdbcUsername("sa")
                .setJdbcPassword("123456")
                .setJobExecutorActivate(false)
                .setHistory(HISTORY_FULL)
                .buildProcessEngine();
    }

    @AfterEach
    void tearDown() {
    }

    void deploy() {
    }


    @Test
    @Deployment(resources = "rbsMulti.bpmn")
    void apply() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("isLeader", true);
        vars.put("cost", 60000.00);
        vars.put("starter", "abt112");
        vars.put("managerList", List.of("abt112", "abt001"));
//        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("rbsMulti", vars);
//        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("rbsMulti").latestVersion().singleResult();
//        final List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().active().processDefinitionId(processDefinition.getId()).list();
//        Assert.notEmpty(list, "list is empty");
//        list.forEach(i -> {
//            System.out.println(i.getProcessInstanceId());
//        });

        runtimeService.deleteProcessInstance("3401", "test delete listener");


    }

    @Test
    void approve() {
        historyService.createHistoricTaskInstanceQuery().finished().list();
    }


    @Test
    void historyQuery() {
        ProcessDefinition processDefinition = getProcessDef("rbsAll");
        log.info("ProcessDefinition -- id: {}, name: {}", processDefinition.getId(), processDefinition.getName());
        final List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processFinished().orderByProcessInstanceId().asc().orderByHistoricActivityInstanceStartTime().asc().list();
        list.forEach(i -> {
            log.info("|-- 活动节点 id: {}, name: {}, assignee: {}, endTime: {}, procInstId: {}", i.getId(), i.getName(), i.getAssignee(), TimeUtil.from(i.getEndTime()), i.getProcessInstanceId());
        });
    }

    @Test
    void historyProcessQuery() {
        ProcessDefinition processDefinition = getProcessDef("rbsAll");
        final List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processDefinitionKey("rbsAll").finished().list();
        Assert.notEmpty(list, "List is empty");
        list.forEach(i -> {
            log.info("|-- 活动流程 id: {}, endTime: {}, deleteReason: {}", i.getId(), TimeUtil.from(i.getEndTime()), i.getDeleteReason());
        });
    }

    ProcessDefinition getProcessDef(String key) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
    }
}