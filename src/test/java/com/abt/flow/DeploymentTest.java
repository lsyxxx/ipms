package com.abt.flow;

import com.abt.flow.model.entity.BizFlowCategory;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 */
@SpringBootTest
@Slf4j
public class DeploymentTest {
    @Autowired
    private RepositoryService repositoryService;


    public static String bpmFile = "processes/MyProcess.bpmn20.xml";


    @Test
    void testDeploySingleFlowFile() throws FileNotFoundException {
        //部署单个流程文件
        Deployment deployment = repositoryService.createDeployment()
                //TIP: 部署后是已经激活的状态：ACT_RE_PROCDEF.SUSPENSION_STATE=1
                //todo: 不知道具体哪一步激活的
                .addInputStream(bpmFile, new FileInputStream(bpmFile))
                //act_re_deployment:key_
                .name("myProcess_")
                .key("myProcess_test")
                .deploy();
        log.info("================= deploy -- id: {}, key: {} =================", deployment.getId(), deployment.getKey());

    }


    public static BizFlowCategory of() {
        BizFlowCategory cat = new BizFlowCategory();
        cat.setId("123");
        cat.setCode("c_123");
        return cat;
    }

    public static void main(String[] args) {
        BizFlowCategory cat1 = of();
        BizFlowCategory cat2 = of();
        System.out.println(cat1.toString());
        System.out.println(cat2.toString());
        System.out.println(cat1 == cat2);
    }
}
