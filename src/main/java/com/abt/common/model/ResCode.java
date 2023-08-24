package com.abt.common.model;

import lombok.Getter;

/**
 * Response code
 */
@Getter
public enum ResCode {
    SUCCESS(0, "0000", "Success"),
    FAIL(1, "9999", "Fail"),
    SESSION_OUT(2, "9998", "Out of Session"),
    //和webapi保持一致
    AUTHENTICATION_FAIL(3, "401", "认证失败，请提供认证信息"),
    INVALID_TOKEN(4, "401", "认证失败，请提供认证信息"),
    ACCESS_DENIED(5, "300", "Access Denied - unauthorized")
    ;

    private final int index;
    /**
     * 业务异常代码
     */
    private final String code;
    /**
     * 异常信息
     */
    private final String message;


    ResCode(int index, String code, String message) {
        this.index = index;
        this.code = code;
        this.message = message;
    }
}
