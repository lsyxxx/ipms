package com.abt.wf.repository.act;

import com.abt.wf.model.act.ActHiTaskInstance;
import com.abt.wf.model.act.ActRuTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActRuTaskRepositoryTest {

    @Autowired
    private ActRuTaskRepository actRuTaskRepository;
    private ActHiTaskInstanceRepository actHiTaskInstanceRepository;

    @Test
    void findByProcInstId() {
        final ActRuTask task = actRuTaskRepository.findByProcInstId("a878241b-0b5b-11ef-8b7d-a497b12f53fd");
        assertNotNull(task);
        System.out.println(task);
    }


    void findDone() {
        //我已完成
    }

    @Test
    void findTask() {
        final ActRuTask actRuTask = actRuTaskRepository.findById("a87f2901-0b5b-11ef-8b7d-a497b12f53fd").orElse(null);
        assertNotNull(actRuTask);
        System.out.println(actRuTask.getAssignee().toString());
        assertNotNull(actRuTask.getAssigneeInfo());
        System.out.println(actRuTask.getAssigneeInfo().toString());
    }

}