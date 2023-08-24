package com.abt.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 *
 */
@Data
@Accessors(chain = true)
public class WebApiToken{

    private String tokenKey = "X-Token";
    private String tokenValue;

    public static WebApiToken of(String tokenValue) {
        return new WebApiToken().setTokenValue(tokenValue);
    }

    public static WebApiToken of() {
        return new WebApiToken();
    }

    public boolean isInvalidToken() {
        return !StringUtils.hasLength(this.tokenValue);
    }

    /**
     * 从spring security的Authentication中获取tokenValue
     * @param authentication
     * @return
     */
    public static WebApiToken of(Authentication authentication) {
        Assert.notNull(authentication.getCredentials(), "Token value must not be null");
        return WebApiToken.of(String.valueOf(authentication.getCredentials()));
    }

}
