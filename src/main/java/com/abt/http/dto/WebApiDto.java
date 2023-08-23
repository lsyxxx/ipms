package com.abt.http.dto;

import com.abt.sys.model.dto.UserView;
import lombok.Data;

/**
 * http返回json数据
 */
@Data
public class WebApiDto {
    private UserView result;
    private String message;
    private int code;

    public static WebApiDto of() {
        return new WebApiDto();
    }
}
