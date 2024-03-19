package com.abt.sys.service;

import com.abt.sys.model.entity.NotifyMessage;

public interface NotifyMessageService {

    /**
     * 发送消息
     */
    void sendMessage(NotifyMessage message);

}
