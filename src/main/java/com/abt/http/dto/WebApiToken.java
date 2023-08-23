package com.abt.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 */
@Data
@Accessors(chain = true)
public class WebApiToken{

    @Value("{webapi.http.token.key}")
    private String tokenKey;
    private String tokenValue;

    public static WebApiToken of(String tokenValue) {
        return new WebApiToken().setTokenValue(tokenValue);
    }

}
