package com.abt.flow.service;

import com.abt.flow.model.entity.FlowSetting;

import java.util.List;

/**
 *
 */
public interface FlowSettingService {
    List<FlowSetting> load();

    void addOne(FlowSetting setting);

    void addReimburseAuditor();
}
