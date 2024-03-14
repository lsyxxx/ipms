package com.abt.common.service.impl;

import com.abt.common.entity.NotifyMessage;
import com.abt.common.service.NotifyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@Slf4j
public class NotifyMessageServiceImpl implements NotifyMessageService {

    @Override
    public void sendMessage(NotifyMessage message) {
        log.info("--- 抄送: " + message.toString());
    }
}
