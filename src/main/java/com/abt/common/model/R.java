package com.abt.common.model;

import com.abt.sys.model.entity.SystemFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.abt.sys.exception.BusinessException;
import com.abt.common.util.JsonUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求返回数据
 * @param <T>
 */
@Data
@Slf4j
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> {
    private T data;
    /**
     * 业务异常代码
     */
    private int code;
    /**
     * 异常信息
     */
    private String msg;

    /**
     * session id
     */
    private String sid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private String token;
    private String httpCode;
    /**
     * 总记录数量
     */
    private int count = 0;
    private int page = 1;
    private int maxPage = 0;
    private long maxSize = 0;
    public static final int DEFAULT_LIMIT = 20;
    private int limit = DEFAULT_LIMIT;

    public R(T data, int code, String message) {
        super();
        this.data = data;
        this.code = code;
        this.msg = message;
    }

    public R(T data, int code, String message, int count) {
        super();
        this.data = data;
        this.code = code;
        this.msg = message;
        this.count = count;
    }
    public R(T data, int code, String message, int count, int limit) {
        super();
        this.data = data;
        this.code = code;
        this.msg = message;
        this.count = count;
        this.limit = limit;
    }

    public R(T data, int code, String message, int count, int page, int limit, int maxPage, long maxSize) {
        super();
        this.data = data;
        this.code = code;
        this.msg = message;
        this.count = count;
        this.page = page;
        this.limit = limit;
        this.maxPage = maxPage;
        this.maxSize = maxSize;
    }

    public T get() {
        return data;
    }



    public static<T> R<T> success(T data) {
        return new R<>(data, ResCode.SUCCESS.getCode(), ResCode.SUCCESS.getMessage());
    }


    public static<T> R<T> success(T data, String msg) {
        return new R<>(data, ResCode.SUCCESS.getCode(), msg);
    }

    public static<T> R<T> success(T data, int count) {
        return new R<>(data, ResCode.SUCCESS.getCode(), ResCode.SUCCESS.getMessage(), count);
    }

    public static<T> R<T> successPage(T data, int maxPage) {
        R<T> r = new R<>(data, ResCode.SUCCESS.getCode(), ResCode.SUCCESS.getMessage());
        r.setMaxPage(maxPage);
        return r;
    }

    public static<T> R<T> success(T data, int count, String msg) {
        return new R<>(data, ResCode.SUCCESS.getCode(), msg, count);
    }

    public static<T> R<T> success(T data, int count, int limit) {
        return new R<>(data, ResCode.SUCCESS.getCode(), ResCode.SUCCESS.getMessage(), count, limit);
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

    public static<T> R<T> fail() {
        return new R<>(null, ResCode.FAIL.getCode(), ResCode.FAIL.getMessage());
    }

    public static<T> R<T> fail(String errMsg, int code) {
        return new R<>(null, code, errMsg == null ? ResCode.FAIL.getMessage() : errMsg);
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

    public static R badRequest() {
        return new R<>(null, ResCode.BAD_REQUEST.getCode(), ResCode.BAD_REQUEST.getMessage());
    }

    public static R badRequest(String msg) {
        return new R<>(null, ResCode.BAD_REQUEST.getCode(), StringUtils.isBlank(msg) ? ResCode.BAD_REQUEST.getMessage() : msg);
    }

    public static<T> R<T> fileNotFound(String errMsg) {
        return new R<>(null, ResCode.FAIL.getCode(), errMsg == null ? ResCode.FILE_NOT_FOUND.getMessage() : errMsg);
    }

    public static<T> R<T> fileNotFound() {
        return new R<>(null, ResCode.FAIL.getCode(), ResCode.FILE_NOT_FOUND.getMessage());
    }

    public static R<List<SystemFile>> noFileUpload() {
        return new R<>(new ArrayList<>(), ResCode.SUCCESS.getCode(), ResCode.FILE_NOT_FOUND.getMessage(), 0, 0);
    }

    public static<T> R<T> invalidParameters(String message) {
        return new R<>(null, ResCode.BAD_REQUEST.getCode(), message);
    }

    public static<T> R<T> warn(String message, T data) {
        return new R<>(null, ResCode.WARN.getCode(), message);
    }

    public static<T> R<T> bizException(T data, String message) {
        return  new R<>(data, ResCode.BIZ_EXCEPTION.getCode(), message);
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
