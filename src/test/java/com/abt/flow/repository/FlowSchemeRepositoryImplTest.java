package com.abt.flow.repository;

import com.abt.flow.model.entity.FlowScheme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FlowSchemeRepositoryImplTest {

    @Autowired
    private FlowSchemeRepository repo;


    @Test
    void findAll() {
        final List<FlowScheme> allEnabled = repo.findAllEnabled();
        Assert.notEmpty(allEnabled, "is Empty");
        System.out.println(allEnabled.size());
        allEnabled.forEach(i -> {
            System.out.println(i.toString());
        });
    }


}