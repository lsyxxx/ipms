package com.abt.flow.repository;

import com.abt.flow.model.entity.FlowScheme;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FlowSchemeRepositoryTest {

    @Autowired
    private FlowSchemeRepository flowSchemeRepository;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void deleteInBatch() {
    }

    @Test
    void find() {
        final List<FlowScheme> all = flowSchemeRepository.findAll();
        Assert.notEmpty(all, "---------- empty");
        all.forEach(i -> {
            i.toString();
        });
    }
}