package com.ipms.sys.service.impl;

import com.ipms.sys.model.entity.RoleFunc;
import com.ipms.sys.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class DatabasePermissionServiceImplTest {

    @Autowired
    DatabasePermissionServiceImpl service;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void loadPermissionBy() {
    }

    @Test
    void addRoleFuncRelations() {
        List<RoleFunc> test = List.of(new RoleFunc[]{
                new RoleFunc().setFuncId(2L).setRoleId(21L).setCreateUser("test"),
                new RoleFunc().setFuncId(2L).setRoleId(22L).setCreateUser("test"),
                new RoleFunc().setFuncId(2L).setRoleId(23L).setCreateUser("test"),
                new RoleFunc().setFuncId(2L).setRoleId(24L).setCreateUser("test"),
                new RoleFunc().setFuncId(2L).setRoleId(25L).setCreateUser("test"),
                new RoleFunc().setFuncId(2L).setRoleId(26L).setCreateUser("test"),
                new RoleFunc().setFuncId(2L).setRoleId(27L).setCreateUser("test"),
        });
        service.addRoleFuncRelations(test);

    }

    @Test
    void insertOne() {
        service.insertOne(new RoleFunc().setFuncId(2L).setRoleId(21L).setCreateUser("test"));
    }
}