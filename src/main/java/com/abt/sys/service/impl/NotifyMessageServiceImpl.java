package com.abt.sys.service.impl;

import com.abt.sys.model.entity.NotifyMessage;
import com.abt.sys.repository.NotifyMessageRepository;
import com.abt.sys.service.NotifyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@Slf4j
public class NotifyMessageServiceImpl implements NotifyMessageService {

    private final NotifyMessageRepository notifyMessageRepository;

    public NotifyMessageServiceImpl(NotifyMessageRepository notifyMessageRepository) {
        this.notifyMessageRepository = notifyMessageRepository;
    }

    @Override
    public void sendMessage(NotifyMessage message) {
        log.info("--- 抄送: {} ", message.toString());
        notifyMessageRepository.save(message);
    }
}
