package com.abt.flow.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BizFlowRelationMapperTest {


    public BizFlowRelation ref;
    @BeforeEach
    void setUp() {
//        ref = new BizFlowRelation()
//                .setBizCategory("rmb_more_5000")
//                .setBizName("报销金额大于5000")
//                .setState(ProcessState.Temporary.code())
//                .setStarterId("user1")
//                .setProcessDefinitionId("procedef_test")
//                .setStarterName("lsy");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void insert() {
    }
}