package com.abt.wxapp.sys.service.impl;

import com.abt.wxapp.sys.entity.SystemSetting;
import com.abt.wxapp.sys.service.SystemSettingService;
import org.springframework.stereotype.Service;

/**
 * 系统配置
 */
@Service
public class SystemSettingServiceImpl implements SystemSettingService {


    @Override
    public SystemSetting findById(String id) {
        return new SystemSetting();
    }
}
