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

    @Test
    void testDistinct() {
        final List<String> list = employeeRepository.findDistinctGroup();
        assertNotNull(list);
        System.out.println(list);
    }

    @Test
    void findDept() {
        final List<EmployeeInfo> list = employeeRepository.findAllWithDept();
        assertNotNull(list);
        list.forEach(i -> {
            System.out.println(String.format("name: %s, dept: %s, deptName: %s", i.getName(), i.getDept(), i.getDeptName()));
        });
    }

}