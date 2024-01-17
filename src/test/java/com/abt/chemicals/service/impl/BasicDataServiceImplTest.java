package com.abt.chemicals.service.impl;

import com.abt.chemicals.entity.ChemicalType;
import com.abt.chemicals.repository.TypeRepository;
import com.abt.chemicals.service.BasicDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


@SpringBootTest
@Slf4j
class BasicDataServiceImplTest {

    @Autowired
    private BasicDataService basicDataService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void queryType() {

    }

    @Test
    void queryType2() {
        final List<ChemicalType> list = basicDataService.queryType("采", 2, null);
        Assert.notEmpty(list, "list is empty");
        list.forEach(i -> {
            log.info("id: {}, parentId: {}, name: {}, childrenSize: {}, parent: {}", i.getId(), i.getParentId(), i.getName(), i.getChildren().size(), i.getParent() == null ? null : i.getParent().getName());
        });
    }

    @Test
    void deleteType() {
        basicDataService.deleteType("");
    }
}