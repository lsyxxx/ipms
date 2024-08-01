package com.abt.oa.service.impl;

import com.abt.oa.OAConstants;
import com.abt.oa.entity.TSystemSetting;
import com.abt.oa.reposity.TSystemSettingRepository;
import com.abt.oa.service.SettingService;
import org.springframework.stereotype.Service;

/**
 * 配置
 */
@Service
public class SettingServiceImpl implements SettingService {

    private final TSystemSettingRepository tSystemSettingRepository;


    public SettingServiceImpl(TSystemSettingRepository tSystemSettingRepository) {
        this.tSystemSettingRepository = tSystemSettingRepository;
    }

    @Override
    public TSystemSetting getTSystemSetting(String id) {
        return tSystemSettingRepository.findById(id).orElse(null);
    }

    @Override
    public TSystemSetting getAttendanceStartDay() {
        return tSystemSettingRepository.findById(OAConstants.attendanceStartDayId).orElse(null);
    }

    @Override
    public TSystemSetting getAttendanceEndDay() {
        return tSystemSettingRepository.findById(OAConstants.attendanceEndDayId).orElse(null);
    }

}
