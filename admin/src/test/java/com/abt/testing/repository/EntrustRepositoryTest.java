package com.abt.testing.repository;

import com.abt.testing.entity.Entrust;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EntrustRepositoryTest {

    @Autowired
    private EntrustRepository entrustRepository;

    @Test
    void findByhtBianHao() {
    }


    @Test
    void findById() {
        final Optional<Entrust> proj = entrustRepository.findById("AJC2023101H002");
        assertTrue(proj.isPresent());
        System.out.println(proj.get().getProjectName());
    }
}