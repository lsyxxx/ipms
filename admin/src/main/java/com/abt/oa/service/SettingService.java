package com.abt.oa.service;

import com.abt.oa.entity.TSystemSetting;

public interface SettingService {
    TSystemSetting getTSystemSetting(String id);

    TSystemSetting getAttendanceStartDay();

    TSystemSetting getAttendanceEndDay();
}
