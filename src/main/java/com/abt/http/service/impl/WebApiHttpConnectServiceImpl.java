package com.abt.http.service.impl;

import com.abt.http.dto.WebApiDto;
import com.abt.http.dto.WebApiToken;
import com.abt.http.handler.IWebApiHttpHandler;
import com.abt.sys.model.dto.UserView;
import com.abt.http.dto.WebApiConfig;
import com.abt.http.service.HttpConnectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
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
@RequiredArgsConstructor
public class WebApiHttpConnectServiceImpl implements HttpConnectService<WebApiDto> {

    private final IWebApiHttpHandler<WebApiDto> webApiHttpConnectHandler;
    private final WebApiConfig webApiConfig;

//    public Object get(String api, WebApiToken token) {
//        WebApiDto<UserView> dto = (WebApiDto) get(api, token);
//        UserView user = dto.get();
//        return user;
//    }

    @Override
    public String simpleGet(String url) {
        return null;
    }



    /**
     * get请求
     * WebApiDto中的result类型不确定，jackson会转成LinkedHashMap()
     * @param api: /xx
     * @param token: WebApiToken
     * @return 有数据则返回WebApiDto, 无则返回null
     */
    @Override
    public WebApiDto get(String api, WebApiToken token) {
        WebApiDto webApiDto;
        try(CloseableHttpClient httpclient = createDefault())  {
            log.info("get uri = {}", this.createUri(api));
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(this.createUri(api)).build();
            webApiHttpConnectHandler.setRequestHeader(httpGet, token);
            webApiDto = httpclient.execute(httpGet, response -> {
                //处理异常的http code
                webApiHttpConnectHandler.handleHttpStatus(response);
                //处理response
                WebApiDto dto = webApiHttpConnectHandler.getHandler(response);
                //将连接释放到连接池中
                EntityUtils.consume(response.getEntity());
                return dto;
            });
        } catch (IOException e) {
            log.error("Get请求失败！url={}, message={}", this.createUri(api), e.getMessage());
            throw new RuntimeException(e);
        }
        return webApiDto;
    }


    /**
     * post 请求
     * WebApiDto中的result类型不确定，jackson会转成LinkedHashMap()
     * @param api
     * @param token
     * @param json json string
     * @return
     */
    @Override
    public WebApiDto post(String api, WebApiToken token, String json) {
        WebApiDto webApiDto;
        try(CloseableHttpClient httpclient = createDefault())  {
            log.info("post uri = {}", this.createUri(api));
            ClassicHttpRequest httPost = ClassicRequestBuilder.post(this.createUri(api))
                    .setEntity(json, ContentType.APPLICATION_JSON)
                    .build();
            webApiHttpConnectHandler.setRequestHeader(httPost, token);
            webApiDto = httpclient.execute(httPost, response -> {
                //处理异常的http code
                webApiHttpConnectHandler.handleHttpStatus(response);
                //处理response
                WebApiDto dto = webApiHttpConnectHandler.postHandler(response);
                //将连接释放到连接池中
                EntityUtils.consume(response.getEntity());
                return dto;
            });
        } catch (IOException e) {
            log.error("Post 请求异常 -- message={}, e.getCause()={}",e.getMessage(), e.getCause());
            throw new RuntimeException(e);
        }
        return webApiDto;
    }

    /**
     * 完整uri
     * @param api: api
     * @return
     */
    @Override
    public String createUri(String api) {
        return webApiConfig.getApiUrl(api);
    }

    @Override
    public WebApiConfig webApiConfig() {
        return this.webApiConfig;
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


}
