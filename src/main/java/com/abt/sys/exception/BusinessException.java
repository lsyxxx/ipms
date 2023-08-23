package com.abt.sys.exception;

/**
 * 业务异常统一处理
 */
public class BusinessException extends RuntimeException{

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
