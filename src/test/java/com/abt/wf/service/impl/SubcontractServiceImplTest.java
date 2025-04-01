package com.abt.wf.service.impl;

import com.abt.wf.entity.SubcontractTesting;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_SUBCONTRACT_TEST;

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
        runtimeService.startProcessInstanceByKey(DEF_KEY_SUBCONTRACT_TEST, "外送申请");


    }
}
