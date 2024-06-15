package com.abt.sys.repository;

import com.abt.sys.model.entity.EmployeeInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void findByIsExit() {
        final List<EmployeeInfo> list = employeeRepository.findByIsExit(true);
        assertNotNull(list);
        System.out.println(list.size());
        list.forEach(System.out::println);
    }
}