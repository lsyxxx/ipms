package com.abt.flow;

import com.abt.flow.service.ReimburseService;
import com.abt.flow.service.impl.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 *
 */
@SpringBootTest
@Slf4j
public class WorkFlowTest extends BaseTest {

    @Autowired
    private ReimburseService service;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    private String procDefId;

    ProcessDefinition rbsDef;
    private Map<String, Object> processVariables;
    String businessKey;
    String starter = "test_lsy";
    @BeforeEach
    void setUp() {

        rbsDef = latestProcDefByName("Normal reimburse less 5000 process");
        procDefId = rbsDef.getId();
        businessKey = "test_case";
    }


    @Test
    void testStartMore() {
        IntStream.range(0, 5).forEach(i -> {
            Authentication.setAuthenticatedUserId(starter);

            ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, businessKey);

            Authentication.setAuthenticatedUserId(null);
            String procId = processInstance.getId();

            Task runningTask = getRunningTask(procId);

            //verify
            Assert.notNull(runningTask, "流程启动失败，没有流程实例 -- id: " + procId);
//
        });
    }



    ProcessDefinition latestProcDefByName(String name) {
        ProcessDefinition def = repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionName(name).singleResult();
        log.info("---最新的 {} 流程定义: id: {}, version: {}", name, def.getId(), def.getVersion());
        return def;
    }

    Task getRunningTask(String procId) {
        return taskService.createTaskQuery().active().processInstanceId(procId).singleResult();
    }


    @Test
    void printTasks() {
        List<Task> tasks = taskService.createTaskQuery().active().list();
        countList(tasks);
        tasks.forEach(i -> logTask(i));
    }


}
