package com.abt.sys.exception;

import lombok.Data;

/**
 * 业务异常统一处理
 */
@Data
public class BusinessException extends RuntimeException{


    private int code;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

}