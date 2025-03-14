package com.abt.salary.repository;

import com.abt.salary.entity.SalaryCell;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SalaryCellRepositoryTest {

    @Autowired
    private SalaryCellRepository repository;

    @Test
    void findByJobNumberOrderByRowIdAndColumnIndexAsc() {
//        final List<SalaryCell> list = repository.findByJobNumberAndYearMonthOrderByRowIdAscColumnIndexAsc("030", "");
//        assertNotNull(list);
//        list.forEach(cell -> {
//            System.out.println(cell.toString());
//        });

    }

    @Test
    void findBySlipIdAndValueIsNotNullOrderByColumnIndexAsc() {

        final List<SalaryCell> list = repository.findBySlipIdAndValueIsNotNullOrderByColumnIndexAsc("c22919a1-6405-45f8-b75c-b88def8a5d66");
        assertNotNull(list);
        System.out.println("=== list.size: " + list.size());
        list.forEach(i -> {
            System.out.printf("name: %s, value: %s%n", i.getName(), i.getValue());
        });
    }

    @Test
    void findByMidAndDepts() {
        List<String> depts = List.of("bf4d0479-856f-4e2c-9c3c-71dbf17794f9", "9c9e5d35-d776-4c31-804c-b72464ec8e7c");
        final List<SalaryCell> list = repository.findByMidAndDepts("202411251732506630211", depts);
        assertNotNull(list);
        System.out.println("=== list.size: " + list.size());
    }
}