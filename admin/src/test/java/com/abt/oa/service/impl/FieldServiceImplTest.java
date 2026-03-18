package com.abt.oa.service.impl;

import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.service.FieldWorkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FieldServiceImplTest {

    @Autowired
    private FieldWorkService fieldWorkService;


    @Test
    void findAllSettings() {
    }

    @Test
    void saveSetting() {

        FieldWorkAttendanceSetting fwas = new FieldWorkAttendanceSetting();
        fwas.setEnabled(true);
        fwas.setName("流体作业2人队长");

        fieldWorkService.saveSetting(fwas);
    }

    @Test
    void find() {
    }
}