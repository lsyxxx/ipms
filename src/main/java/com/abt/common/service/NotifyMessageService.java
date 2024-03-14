package com.abt.common.service;

import com.abt.common.entity.NotifyMessage;

public interface NotifyMessageService {

    /**
     * 发送消息
     */
    void sendMessage(NotifyMessage message);

}
