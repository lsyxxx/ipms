package com.abt.wxapp.sys.service.impl;

import com.abt.wxapp.exception.BusinessException;
import com.abt.wxapp.sys.entity.SystemSetting;
import com.abt.wxapp.sys.repository.SystemSettingRepository;
import com.abt.wxapp.sys.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统配置
 */
@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    @Override
    public SystemSetting findById(String id) {
        return systemSettingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("数据库中未找到该配置参数，ID: " + id));
    }
}
