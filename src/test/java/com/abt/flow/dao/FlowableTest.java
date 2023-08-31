package com.abt.flow.dao;

import com.abt.db.DBConfig;
import com.abt.flow.config.FlowableConfig;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 */
@SpringBootTest
@Slf4j
public class FlowableTest {

    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration ;
    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;


    private static final String bpmnFile = "process/MyProcess.bpmn20.xml";


    @BeforeEach
    void setUp() {
        DataSource ds = processEngineConfiguration.getDataSource();
        log.info("DataSource  == {}", ds.toString());
    }

    @Test
    void testHistory() {
        log.info("test");
    }

    @Test
    void testDeploy() throws FileNotFoundException {
        //更改deployment 必须重新部署，不在在运行中添加
        Deployment deployment = repositoryService.createDeployment()
                .key("Reimburse_more_1000_2")
                .name("Reimburse_more_1000_2")
                .addInputStream(bpmnFile, new FileInputStream(bpmnFile))
                .deploy()
                ;
        log.info("======= Deploy 成功! id: {}", deployment.getId());
    }
}
