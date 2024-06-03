package com.abt.sys.service.impl;

import com.abt.sys.config.SystemConstants;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.repository.SystemMessageRepository;
import com.abt.sys.service.SystemMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@Slf4j
public class SystemMessageServiceImpl implements SystemMessageService {

    private SystemMessageRepository systemMessageRepository;


    @Override
    public void sendMessage(SystemMessage message) {
        log.info("--- 抄送: {} ", message.toString());
        systemMessageRepository.save(message);
    }

    /**
     * 创建一条抄送信息
     */
    @Override
    public SystemMessage createDefaultCopyMessage(String toId, String toName, String href, String content) {
        SystemMessage copyMessage = new SystemMessage();
        copyMessage.setTypeId(SystemConstants.SYSMSG_TYPE_NAME_COPY);
        copyMessage.setTypeName(SystemConstants.SYSMSG_TYPE_ID_COPY);
        copyMessage.setFromId(SystemConstants.SYSTEM_USER_ID);
        copyMessage.setFromName(SystemConstants.SYSTEM_USER_NAME);
        copyMessage.setToId(toId);
        copyMessage.setToName(toName);
        copyMessage.setHref(href);
        copyMessage.setContent(content);

        return copyMessage;
    }
}
