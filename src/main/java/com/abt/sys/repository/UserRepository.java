package com.abt.sys.repository;


import com.abt.common.model.User;
import com.abt.sys.model.dto.UserView;

/**
 * 用户信息
 */
public interface UserRepository {
    UserView getUserBy(String userId);

    /**
     * 仅获取用户id,Account,Name
     * @param userId 用户id
     * @return User
     */
    User getSimpleUserInfo(String userId);
}
