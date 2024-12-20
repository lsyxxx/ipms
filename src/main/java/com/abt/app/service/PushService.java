package com.abt.app.service;

import com.abt.app.entity.PushRegister;

/**
 * 推送服务
 */
public interface PushService {
    /**
     * 注册id
     */
    void register(PushRegister pushRegister);
}
