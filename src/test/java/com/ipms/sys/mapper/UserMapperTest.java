package com.ipms.sys.mapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserMapperTest {

    private UserMapper mapper;

    UserMapperTest(UserMapper mapper) {
        this.mapper = mapper;
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById() {
    }

    @Test
    void findByLoginName() {
    }

    @Test
    void findEnabledById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void count() {
    }

    @Test
    void insert() {
    }

    @Test
    void update() {
        User u = new User();
        u.setId(131L);
        u.setPosition("CEO");
    }
}