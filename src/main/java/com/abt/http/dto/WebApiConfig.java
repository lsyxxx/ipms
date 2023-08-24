package com.abt.http.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 连接webapi http参数
 */

@Data
@Accessors(chain = true)
@Component
public class WebApiConfig {

    @NotNull
    @Value("${webapi.http.ip}")
    private String ip;

    @NotNull
    @Value("${webapi.http.port}")
    private String port;

    /**
     *  不包含api的url
     *  ip:port+baseUrl
     */
    @NotNull
    @Value("${webapi.http.uri}")
    private String url;

    @Value("${webapi.http.base}")
    private String base;

    @NotNull
    @Value("${webapi.http.protocol}")
    private String protocol;

    /**
     * 完整的url
     * 根据protocol, Ip, port, baseUrl, api生成url
     * @return
     */
    private WebApiConfig buildUrl() {
        this.url = protocol + ip + ":" + port + base;
        return this;
    }

    /**
     * 带api的url
     * http://ip:port/base/api
     * @return
     */
    public String getApiUrl(String api) {
        if (!StringUtils.hasLength(this.url)) {
            buildUrl();
        }
        return url + api;
    }

}
