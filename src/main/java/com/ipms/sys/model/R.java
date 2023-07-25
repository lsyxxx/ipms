package com.ipms.sys.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 请求返回数据
 * @param <T>
 */
@Data
@NoArgsConstructor
public class R<T>{
    private T data;
    private String code;
    private String message;
    //eg: 2023-07-25T11:31:14.214514600
    private LocalDateTime timestamp = LocalDateTime.now();

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
        return new R(data, ResCode.SUCCESS.getCode(), ResCode.SUCCESS.getMessage());
    }

    public static<T> R<T> success(String msg) {
        return new R(null, ResCode.SUCCESS.getCode(), msg);
    }

    public static<T> R<T> fail(String errMsg) {
        return new R(null, ResCode.FAIL.getCode(), errMsg == null ? ResCode.FAIL.getMessage() : errMsg);
    }

    public static<T> R<T> invalidSession() {
        return new R<>(null, ResCode.SESSION_OUT.getCode(), ResCode.SESSION_OUT.getMessage());
    }

}
