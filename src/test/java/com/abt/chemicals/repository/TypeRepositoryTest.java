package com.abt.chemicals.repository;

import com.abt.chemicals.entity.ChemicalType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TypeRepositoryTest {

    @Autowired
    private TypeRepository typeRepository;

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
    void findByLevelAndNameLikeOrderBySortAsc() {
        final List<ChemicalType> list = typeRepository.findByLevelAndNameLikeOrderBySortAsc(2, "蜡");

    }
}