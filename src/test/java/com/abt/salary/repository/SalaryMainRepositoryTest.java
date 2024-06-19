package com.abt.salary.repository;

import com.abt.salary.entity.SalaryMain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SalaryMainRepositoryTest {

    @Autowired
    private SalaryMainRepository salaryMainRepository;

    @Test
    void find() {
//        final List<SalaryMain> list = salaryMainRepository.findByYearMonthAndGroupNullable(null, null);
//        assertNotNull(list);
//        System.out.println(list.size());
    }
}