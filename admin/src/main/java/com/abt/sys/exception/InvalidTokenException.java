package com.abt.sys.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 非法token
 */
public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidTokenException(String msg) {
        super(msg);
    }

}
