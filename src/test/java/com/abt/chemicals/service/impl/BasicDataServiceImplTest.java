package com.abt.chemicals.service.impl;

import com.abt.chemicals.repository.TypeRepository;
import com.abt.chemicals.service.BasicDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class BasicDataServiceImplTest {

    @Autowired
    private BasicDataService basicDataService;

    @MockBean
    private TypeRepository typeRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void queryType() {


    }
}