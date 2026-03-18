package com.abt.sys.repository;

import com.abt.sys.model.entity.EmployeeInfo;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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

    @Test
    void findByQuery() {
        final Page<EmployeeInfo> page = employeeRepository.findByQuery(null, false, 0, "ab30277c-f7ba-4a85-b61d-d03d80301646", PageRequest.of(0, 50));
        assertNotNull(page);
        System.out.println(page.getTotalElements());
        page.getContent().forEach(i -> System.out.printf("name: %s, dept: %s\n", i.getName(), i.getDept()));

    }
}