package com.abt.testing.repository;

import com.abt.testing.entity.Entrust;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EntrustRepositoryTest {

    @Autowired
    private EntrustRepository entrustRepository;

    @Test
    void findByhtBianHao() {
        final List<Entrust> contract = entrustRepository.findByhtBianHao("AJC2024065");
        assertNotNull(contract);
        contract.forEach(i -> {
            System.out.println(i.toString());
        });
    }
}