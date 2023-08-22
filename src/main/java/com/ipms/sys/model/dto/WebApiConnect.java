package com.ipms.sys.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * 连接webapi http参数
 */

@Data
@Accessors(chain = true)
public class WebApiConnect {

    @Value("{webapi.http.ip}")
    private String ip;

    @Value("{webapi.http.port}")
    private String port;

    @Value("{webapi.http.url}")
    private String url;

    @Value("{webapi.http.base}")
    private String baseUrl;

    public WebApiConnect build() {
        return this;
    }



}
