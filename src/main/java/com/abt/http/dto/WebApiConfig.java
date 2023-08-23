package com.abt.http.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 连接webapi http参数
 */

@Data
@Accessors(chain = true)
public class WebApiConfig {

    @NotNull
    @Value("{webapi.http.ip}")
    private String ip;

    @NotNull
    @Value("{webapi.http.port}")
    private String port;

    /**
     * 完整url
     *  ip:port+baseUrl
     */
    @NotNull
    @Value("{webapi.http.url}")
    private String url;

    @Value("{webapi.http.base}")
    private String baseUrl;

    @Value("{webapi.http.prefix}")
    private String protocol;

    /**
     * 根据Ip, port, baseUrl生成url
     * @return
     */
    private WebApiConfig buildUrl() {
        this.url = protocol + ip + ":" + port + baseUrl;
        return this;
    }

    public String getUrl() {
        if (!StringUtils.hasLength(this.url)) {
            buildUrl();
        }
        return url;
    }
}
