package com.abt.sys.service;

import com.abt.flow.config.FlowBusinessConfig;
import com.abt.flow.model.entity.FlowSetting;
import com.abt.flow.repository.FlowSettingRepository;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 数据导入
 */
@AllArgsConstructor
public class ImportData {

    private FlowSettingRepository flowSettingRepository;

    /**
     * 流程设置配置
     */
    public void importFlowSettings() {
        //常用审批人
        FlowSetting s1 =defaultAuditors();
        //1. 财务主管 fiManager|U20230406013|冯雅琴
        s1.setType(FlowBusinessConfig.DEFAULT_AUDITOR);
        s1.setKey("fiManager");
        s1.setValue("U20230406013");
        s1.setRemark("冯雅琴");
        flowSettingRepository.save(s1);

        FlowSetting s2 =defaultAuditors();
        //ceo|U20230406002|何爱平
        s2.setKey("ceo");
        s2.setValue("U20230406002");
        s2.setRemark("何爱平");
        flowSettingRepository.save(s2);


        FlowSetting s3 =defaultAuditors();
//        taxOfficer|U20230406007|叶冼婷
        s3.setKey("taxOfficer");
        s3.setValue("U20230406007");
        s3.setRemark("叶冼婷");
        flowSettingRepository.save(s3);


        FlowSetting s4 =defaultAuditors();
//        accountancy|U20230406007|叶冼婷
        s4.setKey("accountancy");
        s4.setValue("U20230406007");
        s4.setRemark("叶冼婷");
        flowSettingRepository.save(s4);


        FlowSetting s5 =defaultAuditors();
//        cashier|U20230406007|叶冼婷
        s5.setKey("cashier");
        s5.setValue("U20230406007");
        s5.setRemark("叶冼婷");
        flowSettingRepository.save(s5);
    }

    void defaultAudit(FlowSetting setting) {
        LocalDateTime now = LocalDateTime.now();
        setting.setCreateDate(now);
        setting.setCreateUserid("45af5ac3-9c89-4244-9f8d-ddc056b0e7b1");
        setting.setCreateUsername("阿伯塔管理员");
        setting.setUpdateDate(now);
        setting.setUpdateUsername("阿伯塔管理员");
        setting.setUpdateUserid("45af5ac3-9c89-4244-9f8d-ddc056b0e7b1");
    }
    FlowSetting defaultAuditors() {
        FlowSetting setting  = new FlowSetting();
        defaultAudit(setting);
        return setting;
    }


    void clear(FlowSetting setting) {
        setting.setRemark("");
        setting.setKey("");
        setting.setId(null);
        setting.setValue("");
    }

}
