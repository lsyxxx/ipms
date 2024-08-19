package com.abt.sys.service.impl;

import com.abt.sys.model.entity.DataPrivilegeRule;
import com.abt.sys.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PermissionServiceImplTest {
    @Autowired
    private PermissionService permissionService;

    @Test
    void getRolesByUserid() {
    }

    @Test
    void getAllRoles() {
    }

    @Test
    void getDataPrivilegeRuleBySourceCode() {
        final DataPrivilegeRule rule = permissionService.getDataPrivilegeRuleBySourceCode("mgrBoard");
        assertNotNull(rule);
    }
}