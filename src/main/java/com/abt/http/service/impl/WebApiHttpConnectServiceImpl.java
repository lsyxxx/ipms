package com.abt.http.service.impl;

import com.abt.common.util.JsonUtil;
import com.abt.http.IHttpClientResponseHandler;
import com.abt.http.dto.WebApiDto;
import com.abt.http.dto.WebApiToken;
import com.abt.http.handler.WebApiHttpHandler;
import com.abt.sys.model.dto.UserView;
import com.abt.http.dto.WebApiConfig;
import com.abt.http.service.HttpConnectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static org.apache.hc.client5.http.impl.classic.HttpClients.*;

/**
 * http连接webapi
 */

@Service
@Slf4j
public class WebApiHttpConnectServiceImpl implements HttpConnectService {


    private final WebApiConfig webApiConfig;
    private final WebApiHttpHandler<WebApiDto> responseHandler;
    public WebApiHttpConnectServiceImpl(WebApiConfig WebApiConfig, WebApiHttpHandler<WebApiDto> responseHandler) {
        this.webApiConfig = WebApiConfig;
        this.responseHandler = responseHandler;
    }

    @Override
    public Object doConnect(String api) {
        //TODO: response返回是Json数据





        //http status(code) 异常
        //直接抛出异常
        //http status(code) 正常
        //组装json, 200正常
        //{"return": {userView},
        //  "message": "操作成功“
        //  "code": 200
        // }
        //返回UserView
        return testAccount();
    }

    @Override
    public String simpleGet(String url) {
        return null;
    }

    private String fullUrl(String api) {
        return getUrl()  + api;
    }


    /**
     * get请求
     * @param api: /xx
     * @param token: WebApiToken
     * @return 有数据则返回WebApiDto, 无则返回null
     */
    @Override
    public Object get(String api, WebApiToken token) {
        try(CloseableHttpClient httpclient = createDefault())  {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(fullUrl(api)).build();
            httpclient.execute(httpGet, response -> {
                responseHandler.setRequestHeader(httpGet, token);
                //处理异常的http code
                responseHandler.handleHttpStatus(response);
                //处理response
                WebApiDto dto = responseHandler.handle(response);
                //将连接释放到连接池中
                EntityUtils.consume(response.getEntity());
                return dto;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String getUrl() {
        return this.webApiConfig.getUrl();
    }

    @Override
    public Object post(String api, WebApiToken token, String json) {
        try(CloseableHttpClient httpclient = createDefault())  {
            ClassicHttpRequest httPost = ClassicRequestBuilder.post(fullUrl(api)).setEntity(json).build();
            httpclient.execute(httPost, response -> {
                responseHandler.setRequestHeader(httPost, token);
                //处理异常的http code
                responseHandler.handleHttpStatus(response);
                //处理response
                WebApiDto dto = responseHandler.handle(response);
                //将连接释放到连接池中
                EntityUtils.consume(response.getEntity());
                return dto;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String createUrl(String api) {
        return getUrl();
    }


    /**
     * 连接数据
     * @return WebApiConfig
     */
    @Bean
    public WebApiConfig WebApiConfig() {
        return new WebApiConfig();
    }





    private UserView testAccount() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_FI"));
        return new UserView().setAccount("abtadmin")
                .setName("abtname")
                .setMobile("17391120673")
                .setPassword("abtpwd")
                .setSex(0)
                .setId("ddc056b0e7b1")
                .setAuthorities(authorities);
    }


    @Bean
    public WebApiHttpHandler<WebApiDto> webApiResponseHandler() {
        return new WebApiHttpHandler<>() {
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
                return JsonUtil.ObjectMapper().readValue(entityStr, WebApiDto.class);
            }

            @Override
            public void setRequestHeader(ClassicHttpRequest request, WebApiToken token) {
                log.info("设置http request header");
                request.setHeader("Content-Type", "application/json;charset=UTF-8");
                request.setHeader(token.getTokenKey(), token.getTokenValue());
            }

            @Override
            public void handleHttpStatus(ClassicHttpResponse response) {
                if (response.getCode() >= 300) {
                    log.error("Http响应异常！Http 响应 = {} {}", response.getCode(), response.getReasonPhrase());
                }
            }

            @Override
            public WebApiDto handle(ClassicHttpResponse response) throws IOException, ParseException {
                log.info("处理http响应结果 -- ttp 响应 = {} {}", response.getCode(), response.getReasonPhrase());
                return null;
            }
        };
    }
}
