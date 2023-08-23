package com.abt.sys.service;

import java.util.List;

/**
 * 权限
 */
public interface PermissionService<T> {

    /**
     * 获取用户权限
     * @param token
     * @return
     */
    List<T> getPermissionsBy(String token);


}
