package com.abt.wf.service.impl;

import com.abt.wf.entity.SubcontractTesting;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 *
 */
@SpringBootTest
public class SubcontractServiceImplTest {

    @Autowired
    private RuntimeService runtimeService;

    void startProcess() {
        SubcontractTesting st = new SubcontractTesting();
        st.setCompany("A");
        st.setCost(5000.00);
    }
}
