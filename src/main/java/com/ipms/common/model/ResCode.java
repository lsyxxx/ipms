package com.ipms.common.model;

import lombok.Getter;

/**
 * Response code
 */
@Getter
public enum ResCode {
    SUCCESS(0, "0000", "Success"),
    FAIL(1, "9999", "Fail"),
    SESSION_OUT(2, "9998", "Out of Session"),
    AUTHENTICATION_FAIL(3, "9997", "Authentication fail"),
    INVALID_TOKEN(4, "9401", "认证失败，请提供认证信息")
    ;

    private final int index;
    private final String code;
    private final String message;

    ResCode(int index, String code, String message) {
        this.index = index;
        this.code = code;
        this.message = message;
    }
}
