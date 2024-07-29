package com.abt.oa.reposity;

import com.abt.oa.entity.FieldWork;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FieldWorkRepositoryTest {

    @Autowired
    private FieldWorkRepository fieldWorkRepository;


    @Test
    void find() {
    }


    @Test
    void save() {

    }

}