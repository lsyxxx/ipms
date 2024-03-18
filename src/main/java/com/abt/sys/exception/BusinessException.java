package com.abt.sys.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 业务异常统一处理
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException{


    private int code;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }


}
