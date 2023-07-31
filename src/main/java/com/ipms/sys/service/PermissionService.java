package com.ipms.sys.service;

import com.ipms.sys.model.entity.User;

/**
 * 功能权限
 */
public interface PermissionService {

    /**
     * 获取用户权限
     * @param user
     */
    void loadPermissionBy(User user);
}
