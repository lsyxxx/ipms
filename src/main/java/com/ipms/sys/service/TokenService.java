package com.ipms.sys.service;


import jakarta.servlet.http.HttpServletRequest;

/**
 * Token 认证
 * @param <T> Token对象
 */
public interface TokenService<T> {

    /**
     * 从HttpServletRequest参数中获取token
     * @return
     */
    T getToken(HttpServletRequest request);

}
