package com.abt.common.model;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Response code
 */
@Getter
public enum ResCode {
    //http code
    SUCCESS(0, HttpStatus.OK.value(), "Success"),


    FAIL(1, 9999, "Failed"),
    SESSION_OUT(2, 9998, "Out of Session"),
    //和webapi保持一致
    AUTHENTICATION_FAIL(3, HttpStatus.UNAUTHORIZED.value(), "认证失败，请提供认证信息"),
    INVALID_TOKEN(4, HttpStatus.UNAUTHORIZED.value(), "认证失败，请提供认证信息"),
    ACCESS_DENIED(5, HttpStatus.UNAUTHORIZED.value(), "Access Denied - unauthorized"),
    BAD_REQUEST(6, HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase()),
    FILE_NOT_FOUND(7, HttpStatus.NOT_FOUND.value(), "文件未找到"),
    NO_FILE_UPLOAD(8, HttpStatus.OK.value(), "用户未上传文件"),
    BIZ_EXCEPTION(9000, HttpStatus.OK.value(), "业务异常"),
    WARN(9, 9997, "Warning")
    ;

    private final int index;
    /**
     * 业务异常代码
     */
    private final int code;
    /**
     * 异常信息
     */
    private final String message;


    ResCode(int index, int code, String message) {
        this.index = index;
        this.code = code;
        this.message = message;
    }
}
