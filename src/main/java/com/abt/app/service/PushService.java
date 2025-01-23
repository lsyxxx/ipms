package com.abt.app.service;

import com.abt.app.entity.JPushRegister;

import java.util.List;

/**
 * 推送服务
 */
public interface PushService {
    List<JPushRegister> findByUserid(String userid);

    /**
     * 注册id
     */
    void register(JPushRegister pushRegister);

    /**
     * 安卓推送消息
     * @param userid 用户id
     * @param alert 消息提示标题
     * @param message 消息提示内容
     * @param badgeAddNum 角标
     */
    void pushAndroid(String userid, String alert, String message, int badgeAddNum);
}
