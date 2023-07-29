package com.ipms.sys.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ipms.common.model.R;
import com.ipms.sys.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
class UserServiceTest {

    @Autowired
    UserService service;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void userList() {
        log.info("user list...");
        List<User> users = service.findAll();
        users.forEach(u -> System.out.println(u.toString()));
    }

    @Test
    void testUserList() {
    }

    @Test
    void findById() {
    }

    @Test
    void count() {
        System.out.println(service.count());
    }

    @Test
    void testFindById() {
    }

    @Test
    void testCount() {
    }

    @Test
    void addUser() {
    }

    public static void main(String[] args) throws JsonProcessingException {
    }
}