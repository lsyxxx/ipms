package com.abt.wf.service;

import com.abt.sys.model.entity.FlowSetting;

import java.util.List;

public interface FlowSettingService {

    List<FlowSetting> findAll();

    void set(FlowSetting setting);

    FlowSetting findById(String id);

    void delete(String id);
}
