package com.abt.sys.service;

import com.abt.common.model.User;
import com.abt.http.dto.WebApiToken;
import com.abt.sys.model.dto.UserRole;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;

/**
 * 基本用户信息
 * @param <T> 用户对象
 * @param <E> 查询条件
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
      */
     WebApiToken getToken(HttpServletRequest request);

     User getSimpleUserInfo(E e);

    User getSimpleUserInfo(String userid);

    List<User> getAllSimpleUser(Integer status);

    User getUserDept(String jobNumber);

     /**
      * 根据userid获取用户信息
      * @param userid User表id
      */
    User getUserDeptByUserid(String userid);
    List<UserRole> getUserRoleByUserid(String userid);
}
