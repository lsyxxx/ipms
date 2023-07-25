package com.ipms.sys.model;

import lombok.Getter;

/**
 * Response code
 */
@Getter
public enum ResCode {
    SUCCESS(0, "0000", "Success"),
    FAIL(1, "9999", "Fail"),
    SESSION_OUT(2, "9998", "Out of Session"),
    AUTHENTICATION_FAIL(3, "9997", "Authentication fail")
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
