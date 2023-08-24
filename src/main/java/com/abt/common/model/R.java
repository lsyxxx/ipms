package com.abt.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.abt.sys.exception.BusinessException;
import com.abt.common.util.JsonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;

/**
 * 请求返回数据
 * @param <T>
 */
@Data
@Slf4j
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> {
    private T result;
    /**
     * 业务异常代码
     */
    private String code;
    /**
     * 异常信息
     */
    private String message;
    //eg: 2023-07-25T11:31:14.214514600
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private String token;
    private String httpCode;

    public R(T resultW, String code, String message) {
        super();
        this.result = result;
        this.code = code;
        this.message = message;
    }

    public T get() {
        return result;
    }

    public static<T> R<T> success(T data) {
        return new R<>(data, ResCode.SUCCESS.getCode(), ResCode.SUCCESS.getMessage());
    }

    public static<T> R<T> success() {
        return new R<>(null, ResCode.SUCCESS.getCode(), ResCode.SUCCESS.getMessage());
    }

    public static<T> R<T> success(String msg) {
        return new R<>(null, ResCode.SUCCESS.getCode(), msg);
    }

    public static<T> R<T> fail(String errMsg) {
        return new R<>(null, ResCode.FAIL.getCode(), errMsg == null ? ResCode.FAIL.getMessage() : errMsg);
    }

    public static<T> R<T> fail(String errMsg, int code) {
        return new R<>(null, String.valueOf(code), errMsg == null ? ResCode.FAIL.getMessage() : errMsg);
    }

    public static<T> R<T> invalidSession() {
        return new R<>(null, ResCode.SESSION_OUT.getCode(), ResCode.SESSION_OUT.getMessage());
    }

    public static R<Exception> authenticationFail(Exception e, String msg) {
        return new R<>(e, ResCode.AUTHENTICATION_FAIL.getCode(), msg);
    }

    public static R<Exception> invalidToken(Exception e) {
        return new R<>(e, ResCode.INVALID_TOKEN.getCode(), ResCode.INVALID_TOKEN.getMessage());
    }

    public static R<String> invalidToken() {
        return new R<>(null, ResCode.INVALID_TOKEN.getCode(), ResCode.INVALID_TOKEN.getMessage());
    }

    public static R<AccessDeniedException> accessDenied() {
        return new R<>(null, ResCode.ACCESS_DENIED.getCode(), ResCode.ACCESS_DENIED.getMessage());
    }

    public static R<AccessDeniedException> accessDenied(AccessDeniedException e) {
        return new R<>(e, ResCode.ACCESS_DENIED.getCode(), ResCode.ACCESS_DENIED.getMessage());
    }


    public String toJson() throws JsonProcessingException {
        try {
            return JsonUtil.toJson(this);
        } catch (Exception exception) {
            log.error("Json序列化异常 -- {}", exception);
            throw new BusinessException("Json序列化异常 -- " + exception.getMessage());
        }
    }


}
