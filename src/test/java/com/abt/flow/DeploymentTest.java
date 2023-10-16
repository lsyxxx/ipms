package com.abt.flow;

import com.abt.flow.model.entity.FlowCategory;
import com.abt.flow.service.impl.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 */
@Slf4j
public class DeploymentTest {
    private RepositoryService repositoryService;
    public static String bpmFile = "processes/Daily_Reimburse.bpmn20.xml";


    @BeforeEach
    void setUp() {
        ProcessEngine processEngine = BaseTest.getProcessEngine();
        this.repositoryService = processEngine.getRepositoryService();
    }

    @Test
    void testDeploySingleFlowFile() throws FileNotFoundException {
        //部署单个流程文件
        Deployment deployment = repositoryService.createDeployment()
                //TIP: 部署后是已经激活的状态：ACT_RE_PROCDEF.SUSPENSION_STATE=1
                //todo: 不知道具体哪一步激活的
                .addInputStream(bpmFile, new FileInputStream(bpmFile))
                //act_re_deployment:key_
                .name("Daily_Reimburse")
                .key("Daily_Reimburse_")
                .deploy();
        log.info("================= deploy -- id: {}, key: {} =================", deployment.getId(), deployment.getKey());

    }

    void deleteProcDef() {
    }


}
