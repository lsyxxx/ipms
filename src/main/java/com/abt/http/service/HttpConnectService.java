package com.abt.http.service;

import com.abt.http.IHttpClientResponseHandler;
import com.abt.http.dto.WebApiToken;

import java.net.MalformedURLException;

/**
 * http连接
 */
public interface HttpConnectService {

    /**
     * http请求URL，并得到返回结果
     */
    Object doConnect(String url);

    /**
     * fluent api
     * 需要注意异常处理
     * @param url
     * @return
     */
    String simpleGet(String url);

    Object get(String url, WebApiToken token);

    Object post(String url, WebApiToken token, String json);

    /**
     * 完整的url: http://ip:port/api/xx
     * @param api: api
     * @return string url
     */
    String createUrl(String api);






}
