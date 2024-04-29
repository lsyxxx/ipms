package com.abt.wf.repository;

import com.abt.wf.entity.Reimburse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;


@SpringBootTest
class ReimburseRepositoryTest {

    @Autowired
    private ReimburseRepository reimburseRepository;

    @Test
    void find() {
    }
}