package com.abt.chemicals.service.impl;

import com.abt.chemicals.entity.ChemicalType;
import com.abt.chemicals.entity.Company;
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
        final List<ChemicalType> list = basicDataService.queryType(null, 2, null);
        Assert.notEmpty(list, "list is empty");
        list.forEach(i -> {
            log.info("id: {}, parentId: {}, name: {}, childrenSize: {}, parent: {}", i.getId(), i.getParentId(), i.getName(), i.getChildren().size(), i.getParent() == null ? null : i.getParent().getName());
        });
    }

    @Test
    void deleteType() {
        basicDataService.deleteType("");
    }

    @Test
    void editType() {
        ChemicalType form = new ChemicalType();
        //add type1
//        form.setName("测试123132");
//        form.setLevel(1);
//        basicDataService.editType(form);
        //edit type1
//        form.setId("e70834af-3307-42bf-829b-6403a3ef9df4");
//        form.setName("测试编辑111");
//        form.setNote("备注jaofjasf");
//        form.setSort(0);
//        basicDataService.editType(form);
        //add type2
//        form.setName("测试添加功能");
//        basicDataService.addType(form);
        //edit type2
        form.setName("测试111222");
        form.setLevel(2);
        form.setParentId("e70834af-3307-42bf-829b-6403a3ef9df4");
        basicDataService.saveType(form);
    }

    @Test
    void queryCompany() {
        basicDataService.queryAllCompanyByType("producer");
    }

    @Test
    void dynamicCompanyQuery() {
        final List<Company> companies = basicDataService.dynamicCompanyQuery("", null, true, 0, 100);
        companies.forEach(i -> log.info(i.toString()));

    }
}