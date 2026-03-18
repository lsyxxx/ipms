package com.abt.wxapp.sys.service;

import com.abt.wxapp.sys.entity.SystemSetting;

public interface SystemSettingService {

    /**
     * 根据id获取系统配置
     * @return SystemSetting 对象
     */
    SystemSetting findById(String id);

}
