package com.abt.finance.repository;

import com.abt.finance.entity.CreditBook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreditBookRepositoryTest {

    @Autowired
    private CreditBookRepository creditBookRepository;

    @Test
    void save() {
        creditBookRepository.save(new CreditBook());
    }

}