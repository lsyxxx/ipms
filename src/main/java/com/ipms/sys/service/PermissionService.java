package com.ipms.sys.service;

import com.ipms.sys.model.entity.RoleFunc;
import com.ipms.sys.model.entity.User;

import java.util.List;

/**
 * 功能权限
 */
public interface PermissionService {

    /**
     * 获取用户权限
     * @param user
     */
    void loadPermissionBy(User user);

    /**
     * 添加角色-权限关系
     */
    void addRoleFuncRelations(List<RoleFunc> list);
}
