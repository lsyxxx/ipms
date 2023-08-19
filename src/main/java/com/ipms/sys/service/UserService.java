package com.ipms.sys.service;

import java.util.Optional;

/**
 * 基本用户信息
 * @param <T> 用户对象
 */
public interface UserService<T, E> {

     Optional<T> userInfoBy(E e);


}
