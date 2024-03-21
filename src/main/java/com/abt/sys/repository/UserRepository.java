package com.abt.sys.repository;


import com.abt.common.model.User;
import com.abt.sys.model.dto.UserView;
import java.util.List;

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


    /**
     * 获取所有用户
     * criteria
     * @param userStatus 用户是否启用
     */
    List<User> getAllSimpleUser(Integer userStatus);
}
