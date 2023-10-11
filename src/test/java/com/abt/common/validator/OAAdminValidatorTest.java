package com.abt.common.validator;

import com.abt.sys.model.dto.UserView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OAAdminValidatorTest {

    @Autowired
    private OAAdminValidator validator;
    private UserView user;

    @BeforeEach
    void setUp() {
        user = new UserView();
        user.setId("45af5ac3-9c89-4244-9f8d-ddc056b0e7b1");
        user.setName("阿伯塔管理员");
        user.setAccount("abtadmin");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void validate() {
        final ValidationResult result = validator.validate(user);
        System.out.println(result.toString());
    }
}