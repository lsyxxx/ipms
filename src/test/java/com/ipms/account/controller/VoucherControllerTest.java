package com.ipms.account.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@WithMockUser(username="admin",roles={"USER"})
class VoucherControllerTest {

    private MockMvc mvc;
    private
    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new VoucherController()).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findAll() throws Exception {
        this.mvc.perform(get("/ac/a/vc/all"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

}