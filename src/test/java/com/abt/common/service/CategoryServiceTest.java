package com.abt.common.service;

import com.abt.common.entity.Category;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    void findByTypeId() {
        final List<Category> list = categoryService.findByTypeId("FieldWork_ComponentType");
        assertNotNull(list);
        System.out.println(list.size());
        list.forEach(System.out::println);


    }
}