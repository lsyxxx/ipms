package com.abt.http.service;

import com.abt.http.dto.WebApiConfig;
import com.abt.http.dto.WebApiToken;

/**
 * http连接
 * @return T http返回对象
 */
public interface HttpConnectService<T> {
    /**
     * fluent api
     * 需要注意异常处理
     * @param url
     * @return
     */
    String simpleGet(String url);

    T get(String url, WebApiToken token);

    T post(String url, WebApiToken token, String json);

    /**
     * 完整的uri
     * ip:port/base/api
     * @param api: api
     * @return string url
     */
    String createUri(String api);

    WebApiConfig webApiConfig();






}
