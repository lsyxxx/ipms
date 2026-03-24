package com.abt.sys.exception;

/**
 * 系统异常
 */
public class SystemException extends RuntimeException {

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String message, Exception e) {
        super(message);
    }
}
