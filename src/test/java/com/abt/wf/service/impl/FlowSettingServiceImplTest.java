package com.abt.wf.service.impl;

import com.abt.sys.model.entity.FlowSetting;
import com.abt.wf.service.FlowSettingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FlowSettingServiceImplTest {

    @Autowired
    private FlowSettingService flowSettingService;

    @Test
    void testInsert() {
        FlowSetting setting = new FlowSetting();
        setting.setDescription("默认抄送人");
        setting.setKey("rbsDefaultCopy");
        //U20230406001 董事长,U20230406002，总经理, U20230406003:柳小明,U20230406004:董雯, U20230406005:杨士荣
        setting.setValue("621faa40-f45c-4da8-9a8f-65b0c5353f40");
        setting.setRemark("刘宋菀");
        setting.setType("rbsDefaultCopy");
        flowSettingService.set(setting);
//        FlowSetting s2 = new FlowSetting();
//        s2.setDescription("默认抄送人");
//        s2.setKey("rbsDefaultCopy");
//        //U20230406001 董事长,U20230406002，总经理, U20230406003:柳小明,U20230406004:董雯, U20230406005:杨士荣
//        s2.setValue("U20230406002");
//        s2.setRemark("何爱平");
//        s2.setType("rbsDefaultCopy");
//        flowSettingService.set(s2);
    }
}