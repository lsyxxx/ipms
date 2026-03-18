package com.abt.http.handler;

import com.abt.common.util.JsonUtil;
import com.abt.http.dto.WebApiDto;
import com.abt.http.dto.WebApiToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 使用Apache HttpClient进行Post/Get请求
 */

@Component
@Slf4j
public class WebApiHttpConnectHandler implements IWebApiHttpHandler<WebApiDto> {

    @Override
    public WebApiDto getHandler(ClassicHttpResponse response) throws IOException, ParseException {
        log.info("处理 Get 请求");
        final HttpEntity responseEntity = response.getEntity();
        WebApiDto dto = WebApiDto.of();
        if (responseEntity == null) {
            return dto;
        }
        String entityStr = EntityUtils.toString(responseEntity);
        return JsonUtil.ObjectMapper().readValue(entityStr, WebApiDto.class);
    }

    @Override
    public WebApiDto postHandler(ClassicHttpResponse response) throws IOException, ParseException {
        log.info("处理 Post 请求");
        final HttpEntity responseEntity = response.getEntity();
        WebApiDto dto = WebApiDto.of();
        if (responseEntity == null) {
            return dto;
        }
        String entityStr = EntityUtils.toString(responseEntity);
        log.trace("post handler return string = {}", entityStr);
        return JsonUtil.ObjectMapper().readValue(entityStr, WebApiDto.class);
    }

    @Override
    public void setRequestHeader(ClassicHttpRequest request, WebApiToken token) {
        log.info("设置http request header");
        request.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        request.setHeader("Accept-Encoding", "gzip, deflate");
        log.trace("token={}", token.toString());
        request.setHeader(token.getTokenKey(), token.getTokenValue());
    }

    @Override
    public void handleHttpStatus(ClassicHttpResponse response) {
        log.info("处理http状态 -- code={}", response.getCode());
        if (response.getCode() >= 300) {
            log.warn("Http响应异常！Http 响应 = {} {}", response.getCode(), response.getReasonPhrase());
        }
    }

    @Override
    public WebApiDto handle(ClassicHttpResponse response) {
        log.info("处理http响应结果 -- ttp 响应 = {} {}", response.getCode(), response.getReasonPhrase());
        return null;
    }
}
