package com.abt.testing.controller;

import com.abt.chkmodule.service.CheckModuleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CheckSettingControllerTest {

    @Autowired
    private CheckModuleService checkModuleService;

    @Test
    void testDel() {
    }

}