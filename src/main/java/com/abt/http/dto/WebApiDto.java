package com.abt.http.dto;

import com.abt.sys.model.dto.UserView;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * http返回json数据
 */
@Data
public class WebApiDto<T> {
    private T result;
    private String message;
    private int code;

    public static WebApiDto of() {
        return new WebApiDto();
    }

    public boolean isFail() {
        return code != HttpStatus.OK.value();
    }

    public boolean hasResult() {
        return result != null;
    }

    public T get() {
        return result;
    }
}
