package com.abt.sys.exception;

/**
 * client 请求参数错误
 */
public class BadRequestParameterException extends RuntimeException{

    public BadRequestParameterException(String message) {
        super(message);
    }
}
