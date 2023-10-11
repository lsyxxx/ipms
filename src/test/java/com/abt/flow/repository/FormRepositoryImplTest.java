package com.abt.flow.repository;

import com.abt.flow.model.entity.Form;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FormRepositoryImplTest {

    @Autowired
    private FormRepositoryImpl repo;
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findById() {
        final Form byId = repo.findById("51ed5ed3-45a3-4f6e-8c17-0d0a884112b3");
        System.out.println(byId.toString());

    }
}