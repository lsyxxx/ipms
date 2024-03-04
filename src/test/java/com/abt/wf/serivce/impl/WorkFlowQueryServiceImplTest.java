package com.abt.wf.serivce.impl;

import com.abt.wf.model.TaskDTO;
import com.abt.wf.serivce.WorkFlowQueryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class WorkFlowQueryServiceImplTest {
    @Autowired
    private WorkFlowQueryService workFlowQueryService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void queryMyRbs() {
//        workFlowQueryService.queryMyRbs("621faa40-f45c-4da8-9a8f-65b0c5353f40", 0, 20);

    }

    @Test
    void queryTaskListByStartUserid() {
//        final List<TaskDTO> tasks = workFlowQueryService.queryMyRbs("demo",  0, 100);
//        tasks.forEach(t -> {
//            log.info("TaskDTO: procId: {}, procDefId: {}, taskName:{} taskId: {}, assignee: {}, taskEndTime: {}", t.getProcessInstanceId(), t.getProcessDefinitionId(), t.getTaskDefName(),  t.getTaskInstanceId(), t.getAssigneeId(), t.getTaskEndTime());
//        });

    }

    @Test
    void testQueryMyRbs() {
    }

    @Test
    void queryMyTodoList() {
    }

    @Test
    void queryMyDoneList() {
    }

    @Test
    void queryProcessInstanceLog() {
    }

    @Test
    void queryUserTaskBpmnModelExtensionProperties() {
    }
}