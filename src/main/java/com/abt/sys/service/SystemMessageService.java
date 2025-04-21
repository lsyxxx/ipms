package com.abt.sys.service;

import com.abt.sys.model.dto.SystemMessageRequestForm;
import com.abt.sys.model.entity.SystemMessage;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SystemMessageService {

    /**
     * 发送消息
     */
    void sendMessage(SystemMessage message);

    SystemMessage createDefaultCopyMessage(String toId, String toName, String href, String content, String service);

    //获取用户的所有抄送信息
    Page<SystemMessage> findUserSystemMessagesAllPageable(SystemMessageRequestForm requestForm);

    void readOne(String id);

    /**
     * 提醒消息
     */
    SystemMessage createSystemMessage(String toId, String toName, String href, String content, String service);

    void readAll(String toId);

    void sendAll(List<SystemMessage> list);
}
