package com.ipms.sys.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class RoleServiceTest {

    @Autowired
    private RoleService roleService;
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findEnabledById() {
    }

    @Test
    void findAll() {
        List<Role> roles = roleService.findAll();
        log.debug("list size == {}", roles.size());
        roles.forEach(r -> System.out.println(r.toString()));
    }
}