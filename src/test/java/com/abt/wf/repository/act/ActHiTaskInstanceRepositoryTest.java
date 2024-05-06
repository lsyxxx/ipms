package com.abt.wf.repository.act;

import com.abt.wf.model.act.ActHiTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActHiTaskInstanceRepositoryTest {
    @Autowired
    private ActHiTaskInstanceRepository actHiTaskInstanceRepository;

    @Test
    void find() {
        final ActHiTaskInstance actHiTaskInstance = actHiTaskInstanceRepository.findById("a87f2901-0b5b-11ef-8b7d-a497b12f53fd").orElse(null);
        assertNotNull(actHiTaskInstance);
        System.out.println(actHiTaskInstance.getProcInstance().toString());
    }

}