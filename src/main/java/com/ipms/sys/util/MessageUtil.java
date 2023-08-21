package com.ipms.sys.util;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.core.SpringSecurityMessageSource;

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
}
