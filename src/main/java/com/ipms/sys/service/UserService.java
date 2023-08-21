package com.ipms.sys.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.net.http.HttpRequest;
import java.util.Optional;

/**
 * 基本用户信息
 * @param <T> 用户对象
 */
public interface UserService<T, E> {

     /**
      * 获取user info
      * @param e: 查询条件
      * @return: Optional对象
      */
     Optional<T> userInfoBy(E e);

     /**
      * 从request header中获取token
      * @param request
      * @return
      */
     String getToken(HttpServletRequest request);


}
