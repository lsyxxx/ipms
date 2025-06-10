package com.abt.sys.service.impl;

import com.abt.sys.model.entity.DataPrivilegeRule;
import com.abt.sys.model.entity.Role;
import com.abt.sys.repository.DataPrivilegeRuleRepository;
import com.abt.sys.repository.RoleRepository;
import com.abt.sys.service.PermissionService;
import com.abt.sys.util.WithQueryUtil;
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
    private final DataPrivilegeRuleRepository dataPrivilegeRuleRepository;


    public PermissionServiceImpl(RoleRepository roleRepository, DataPrivilegeRuleRepository dataPrivilegeRuleRepository) {
        this.roleRepository = roleRepository;
        this.dataPrivilegeRuleRepository = dataPrivilegeRuleRepository;
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

    @Override
    public DataPrivilegeRule getDataPrivilegeRuleBySourceCode(String sourceCode) {
        return WithQueryUtil.build(dataPrivilegeRuleRepository.findBySourceCodeAndEnable(sourceCode, true));
    }

}
