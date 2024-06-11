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
    void findAllSalaryEnabled() {
        final List<EmployeeInfo> list = employeeRepository.findAllSalaryEnabled();
//        final List<EmployeeInfo> list = employeeRepository.findAll();
        assertNotNull(list);
        System.out.println("=== 启用工资人数: " + list.size());
    }
}