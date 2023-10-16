package com.abt.flow.service;

import com.abt.common.model.User;
import com.abt.flow.model.entity.FlowSetting;

import java.util.List;

/**
 *
 */
public interface FlowSettingService {
    List<FlowSetting> load();

    void addOne(FlowSetting setting);

    void addReimburseAuditor();

    void addDefaultFlowSkip();

    /**
     * 是否是领导角色，跳过基础审批
     * @param user 用户
     */
    boolean isManager(User user);
}
