package com.ipms.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 请求返回数据
 * @param <T>
 */
@Data
@Slf4j
@NoArgsConstructor
public class R<T> {
    private T data;
    /**
     * 业务异常代码
     */
    private String code;
    /**
     * 异常信息
     */
    private String message;
    //eg: 2023-07-25T11:31:14.214514600
    private LocalDateTime timestamp = LocalDateTime.now();
    private String token;
    private String httpCode;

    public R(T data, String code, String message) {
        super();
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public T get() {
        return data;
    }

    public static<T> R<T> success(T data) {
        return new R<>(data, ResCode.SUCCESS.getCode(), ResCode.SUCCESS.getMessage());
    }

    public static<T> R<T> success(String msg) {
        return new R(null, ResCode.SUCCESS.getCode(), msg);
    }

    public static<T> R<T> fail(String errMsg) {
        return new R<>(null, ResCode.FAIL.getCode(), errMsg == null ? ResCode.FAIL.getMessage() : errMsg);
    }

    public static<T> R<T> invalidSession() {
        return new R<>(null, ResCode.SESSION_OUT.getCode(), ResCode.SESSION_OUT.getMessage());
    }

    public static R<Exception> authenticationFail(Exception e,  String msg) {
        return new R<>(e, ResCode.AUTHENTICATION_FAIL.getCode(), msg);
    }

    /**
     * TODO to json str
     * @return
     */
    public String toJson() {
        return "";
    }


}
