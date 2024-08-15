package com.abt.sys.service;

import com.abt.sys.model.entity.Role;

import java.util.HashSet;

/**
 * 权限
 */
public interface PermissionService {

    HashSet<Role> getRolesByUserid(String userid);

    HashSet<Role> getAllRoles();
}
