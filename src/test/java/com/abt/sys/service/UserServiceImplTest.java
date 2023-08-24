package com.abt.sys.service;

import com.abt.http.dto.WebApiToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Optional;

@SpringBootTest
@Slf4j
class UserServiceImplTest {

    @Autowired
    private UserService service;
    WebApiToken token;

    @BeforeEach
    void setUp() {

        token = WebApiToken.of();
        token.setTokenKey("X-Token");
        token.setTokenValue("9c22c407");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testGetUserInfo() {
        System.out.println(token.toString());
        Optional user = service.userInfoBy(token);
        user.ifPresent(u -> System.out.println(u.toString()));

        System.out.println(user.get());
    }


}