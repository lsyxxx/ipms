package com.abt.oa.service;

import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;

import java.util.List;

public interface FieldWorkService {
    List<FieldWorkAttendanceSetting> findAllSettings();

    void saveSetting(FieldWorkAttendanceSetting fieldAttendanceSetting);

    /**
     * 查询所有可用的补助项目
     */
    List<FieldWorkAttendanceSetting> findAllEnabledAllowance();

    void saveAttendance(FieldWork fwa);
}
