package com.abt.flow.controller;

import com.abt.common.util.MessageUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.MessageSourceAccessor;

import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.*;

class FlowControllerTest {


    protected MessageSourceAccessor messages = MessageUtil.getAccessor();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void flowList() {
    }

    @Test
    void testMessage() {
        String type = MessageUtil.format("flow.FlowController.flowList", "type_123");
//        String type = MessageFormat.format("test param - {0}", "type123");
        System.out.println(type);
    }
}