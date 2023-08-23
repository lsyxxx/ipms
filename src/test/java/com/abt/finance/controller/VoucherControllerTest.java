package com.abt.finance.controller;

import com.abt.security.SecurityConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//spring security test
@ExtendWith(SpringExtension.class)
@WithMockUser(username="admin",roles={"USER"})
@ContextConfiguration(classes = SecurityConfig.class)
@WebAppConfiguration
class VoucherControllerTest {


    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    /**
     * mock 2种方法初始化
     */
    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                //初始化1：point to Spring configuration with Spring MVC and controller infrastructure in it.
                .webAppContextSetup(context)
                .apply(springSecurity())
                //初始化方法2： point directly to the controllers you want to test and programmatically configure Spring MVC infrastructure
                //不加载spring配置
//                .standaloneSetup(new VoucherController())
                .build();
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