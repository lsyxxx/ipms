package com.abt.common.util;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 *
 */
public class MessageUtil extends ResourceBundleMessageSource {

    public MessageUtil() {
        setBasename("i18n.message");
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new MessageUtil());
    }

    public static String getMessage(String code) {
        return getAccessor().getMessage(code);
    }
}
