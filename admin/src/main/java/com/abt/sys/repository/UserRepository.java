package com.abt.sys.repository;


import com.abt.common.model.User;
import com.abt.sys.model.dto.UserRole;
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

    /**
     * 根据工号获取用户基本信息，包含部门及科室
     * @param jobNumber 工号
     */
    User getEmployeeDeptByJobNumber(String jobNumber);

    /**
     * 根据用户id(User表)获取
     * @param userid User表id
     */
    User getEmployeeDeptByUserid(String userid);

    List<UserRole> getUserRoleByUserid(String userid);

    /**
     * 获取角色用户
     * @param roleId 角色id
     */
    List<UserRole> getUserByRole(String roleId);
}
