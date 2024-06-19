package com.abt.salary.repository;

import com.abt.salary.entity.SalaryCell;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
}