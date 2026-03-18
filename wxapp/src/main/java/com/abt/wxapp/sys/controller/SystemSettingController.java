package com.abt.wxapp.sys.controller;

import com.abt.wxapp.common.model.R;
import com.abt.wxapp.sys.entity.SystemSetting;
import com.abt.wxapp.sys.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统配置接口
 */
@Slf4j
@RestController
@RequestMapping("/sys/setting")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    /**
     * 根据 ID 获取系统配置参数
     */
    @GetMapping("/id")
    public R<SystemSetting> getById(@PathVariable("id") String id) {
        log.info("接收到查询系统配置的请求，参数 ID:", id);
        SystemSetting setting = systemSettingService.findById(id);
        return R.success(setting);
    }
}