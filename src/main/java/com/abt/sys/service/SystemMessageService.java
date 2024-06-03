package com.abt.sys.service;

import com.abt.sys.model.entity.SystemMessage;

public interface SystemMessageService {

    /**
     * 发送消息
     */
    void sendMessage(SystemMessage message);

    SystemMessage createDefaultCopyMessage(String toId, String toName, String href, String content);
}
