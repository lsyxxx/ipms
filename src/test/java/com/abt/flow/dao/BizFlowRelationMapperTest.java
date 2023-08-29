package com.abt.flow.dao;

import com.abt.flow.model.ProcessState;
import com.abt.flow.model.entity.BizFlowRelation;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BizFlowRelationMapperTest {

    @Autowired
    private BizFlowRelationMapper mapper;

    public BizFlowRelation ref;
    @BeforeEach
    void setUp() {
        ref = new BizFlowRelation()
                .setBizCategory("rmb_more_5000")
                .setBizName("报销金额大于5000")
                .setState(ProcessState.Temporary.code())
                .setStarterId("user1")
                .setProcessDefinitionId("procedef_test")
                .setStarterName("lsy");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void insert() {
        mapper.insert(ref);


    }
}