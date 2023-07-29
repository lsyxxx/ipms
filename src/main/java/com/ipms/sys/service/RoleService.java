package com.ipms.sys.service;

import com.ipms.sys.mapper.RoleMapper;
import com.ipms.sys.model.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleMapper roleMapper;

    public RoleService(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }


    public Role findEnabledById(Long id) {
        return roleMapper.findEnabledBy(id);
    }

    public List<Role> findAll() {
        return roleMapper.findAll();
    }

}
