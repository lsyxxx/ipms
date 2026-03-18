package com.abt.wxapp.exception;


import org.springframework.security.core.AuthenticationException;

/**
 * token认证失败
 */
public class InvalidTokenException extends AuthenticationException {
    public InvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidTokenException(String msg) {
        super(msg);
    }
}
