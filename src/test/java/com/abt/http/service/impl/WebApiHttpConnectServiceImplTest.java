package com.abt.http.service.impl;

import com.abt.common.util.JsonUtil;
import com.abt.http.dto.WebApiDto;
import com.abt.http.dto.WebApiToken;
import com.abt.http.service.HttpConnectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.core.util.Json;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;


@SpringBootTest
@Slf4j
class WebApiHttpConnectServiceImplTest {

    @Autowired
    private HttpConnectService webApiHttpConnectService;


    @BeforeEach
    void setUp() {
//        this.webApiHttpConnectService = new WebApiHttpConnectServiceImpl(webApiConfig, webApiHttpHandler);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void doConnect() {
    }

    @Test
    void simpleGet() {
    }

    @Test
    void get() {
    }

    @Test
    void post() throws JsonProcessingException {
        String uri = "http://7386n59g91.goho.co:10008/api/roles/update";
//        String postJson = "{\"name\":\"测试测试88\",\"status\":0,\"createTime\":\"2023-08-11 09:33:01\",\"createId\":\"\",\"typeName\":\"\",\"typeId\":\"\",\"id\":\"248e02da-15e9-4026-a4a2-746020d7497c\"}";

        UpdateUser user = new UpdateUser();
        user.setName("测试测试99")
                .setStatus(0)
                .setCreateTime(LocalDateTime.now())
                .setId("45af5ac3-9c89-4244-9f8d-ddc056b0e7b1")
                ;
        String postJson = JsonUtil.toJson(user);
        log.info("postJson={}", postJson);
        WebApiToken token = new WebApiToken();
        token.setTokenKey("X-token");
        token.setTokenValue("9c22c407");
        String api = "/check/GetUserProfile";
        WebApiDto dto;
        dto = (WebApiDto) webApiHttpConnectService.get(api, token);
        System.out.println(dto.toString());
    }

    @Test
    void createUrl() {
    }


    @Data
    @Accessors(chain = true)
    class UpdateUser {
        private String name;
        private int status;
        private LocalDateTime createTime;
        private String createId;
        private String typeName;

        private String typeId;
        private String id;
    }
}