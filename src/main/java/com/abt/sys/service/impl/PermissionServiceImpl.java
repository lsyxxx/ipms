package com.abt.sys.service.impl;

import com.abt.sys.model.entity.Role;
import com.abt.sys.repository.RoleRepository;
import com.abt.sys.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashSet;

/**
 *
 */
@Service
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final RoleRepository roleRepository;

    public PermissionServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public HashSet<Role> getRolesByUserid(String userid) {
        Assert.hasText(userid, "userid不能为空!");
        return roleRepository.getRolesByUserid(userid);
    }


    @Override
    public HashSet<Role> getAllRoles() {
        return new HashSet<>(roleRepository.findAll());
    }
}
