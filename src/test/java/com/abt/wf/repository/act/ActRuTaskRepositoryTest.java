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
        final ActRuTask task = actRuTaskRepository.findByProcInstId("sss");
//        assertNotNull(task);
        System.out.println(task.toString());
    }


    void findDone() {
        //我已完成
    }
}