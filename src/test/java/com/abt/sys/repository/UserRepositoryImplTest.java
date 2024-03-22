package com.abt.sys.repository;

import com.abt.common.model.User;
import com.abt.sys.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Slf4j
class UserRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void getEmployeeDeptByJobNumber() {
        final User user = userRepository.getEmployeeDeptByJobNumber("112");
        System.out.println(user.toString());
    }
}