package com.ipms.sys.service;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * 资源文件
 */
@Service
public class MessageService {

    private final MessageSource messageSource;

    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getBy(String name) {
        return messageSource.getMessage(name, null, Locale.getDefault());
    }
}
