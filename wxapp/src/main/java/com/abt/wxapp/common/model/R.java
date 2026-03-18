package com.abt.wxapp.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * 返回数据
 */
@Data
@Slf4j
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> {

    /**
     * 异常代码
     */
    private int code = HttpStatus.OK.value();;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    /**
     * session id
     */
    private String sid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    private String token;

    public R() {
        super();
    }


    public R(T data, String message) {
        super();
        this.data = data;
        this.msg = message;
    }

    public R(T data, int code, String message) {
        super();
        this.data = data;
        this.code = code;
        this.msg = message;
    }

    /**
     * 无返回数据成功(200)
     * @return
     * @param <T>
     */
    public static<T> R<T> success() {
        return new R<>(null, HttpStatus.OK.getReasonPhrase());
    }

    /**
     * 返回数据成功(200)
     * @param data
     * @return
     * @param <T>
     */
    public static<T> R<T> success(T data) {
        return new R<>(data, HttpStatus.OK.getReasonPhrase());
    }

    /**
     * 无法返回数据失败(500)
     * @param message
     * @return
     * @param <T>
     */
    public static<T> R<T> fail(String message) {
        return new R<>(null, HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    /**
     * 返回数据失败(500)
     * @param data
     * @param message
     * @return
     * @param <T>
     */
    public static<T> R<T> fail(T data, String message) {
        return new R<>(data, HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    public static R<Exception> authenticationFail(Exception e, String msg) {
        return new R<>(e, HttpStatus.UNAUTHORIZED.value(), msg);
    }


}
