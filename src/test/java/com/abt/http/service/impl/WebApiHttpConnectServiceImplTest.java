package com.abt.http.service.impl;

import com.abt.http.dto.WebApiConfig;
import com.abt.http.dto.WebApiDto;
import com.abt.http.dto.WebApiToken;
import com.abt.http.handler.WebApiHttpHandler;
import com.abt.http.service.HttpConnectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Slf4j
@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class WebApiHttpConnectServiceImplTest {

    @Mock
    private WebApiHttpConnectServiceImpl webApiHttpConnectService;
    @InjectMocks
    private final WebApiHttpHandler<WebApiDto> webApiHttpHandler;
    @InjectMocks
    private final WebApiConfig webApiConfig;



    @BeforeEach
    void setUp() {
        this.webApiHttpConnectService = new WebApiHttpConnectServiceImpl(webApiConfig, webApiHttpHandler);
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
    void post() {
        String url = "http://7386n59g91.goho.co:10008/api/roles/update";
        String postJson = "{\"name\":\"测试测试77\",\"status\":0,\"createTime\":\"2023-08-11 09:33:01\",\"createId\":\"\",\"typeName\":\"\",\"typeId\":\"\",\"id\":\"248e02da-15e9-4026-a4a2-746020d7497c\"}";

        WebApiDto dto = (WebApiDto) webApiHttpConnectService.post(url, WebApiToken.of("eb8d981c"), postJson);
        System.out.println(dto.getMessage());
    }

    @Test
    void createUrl() {
    }
}