package com.ipms.sys.service;

/**
 * http连接
 */
public interface HttpConnectService {

    /**
     * 生成一个带token header的request url
     * @param tokenValue
     * @return
     */
    String createUrlWithToken(String tokenValue);


    /**
     * http请求URL，并得到返回结果
     * @param url
     */
    Object doConnect(String url);


}
