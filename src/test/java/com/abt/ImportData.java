package com.abt;

import com.abt.flow.config.FlowBusinessConfig;
import com.abt.flow.model.entity.FlowSetting;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

/**
 * 数据导入
 */
public class ImportData {


    /**
     * 流程设置配置
     */
    @Rollback(value = false)
    void importFlowSettings() {
        FlowSetting s1 = new FlowSetting();
        //常用审批人
        //1. 财务主管 fiManager|U20230406013|冯雅琴
        s1.setType(FlowBusinessConfig.DEFAULT_AUDITOR);
        s1.setKey("fiManager");
        s1.setValue("U20230406013");
        s1.setRemark("冯雅琴");
        defaultAudit(s1);


    }

    void defaultAudit(FlowSetting setting) {
        LocalDateTime now = LocalDateTime.now();
        setting.setCreateDate(now);
        setting.setCreateUserid("45af5ac3-9c89-4244-9f8d-ddc056b0e7b1");
        setting.setCreateUsername("阿伯塔管理员");
        setting.setUpdateDate(now);
        setting.setCreateUsername("阿伯塔管理员");
        setting.setUpdateUserid("45af5ac3-9c89-4244-9f8d-ddc056b0e7b1");
    }

    void clear() {

    }

}
