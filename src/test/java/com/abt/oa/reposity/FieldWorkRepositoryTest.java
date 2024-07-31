package com.abt.oa.reposity;

import com.abt.oa.entity.FieldWork;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FieldWorkRepositoryTest {

    @Autowired
    private FieldWorkRepository fieldWorkRepository;


    @Test
    void find() {
    }


    @Test
    void save() {

    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findByCreateUseridAndAttendanceDate() {
    }

    @Test
    void findTodoByQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        final Page<FieldWork> page = fieldWorkRepository.findTodoFetchedByQuery("流体", "U20230406006", null, null, null, pageable);
        System.out.printf("list size = %d\n", page.getContent().size());
        page.getContent().forEach(i -> {
            System.out.printf("|-main: name: %s, date:%s, ids: %s \n", i.getUsername(), i.getAttendanceDate().toString(), i.getItemIds());
            i.getItems().forEach(j -> {
                System.out.printf("|----item: name: %s, prod: %s. meal: %s, sum: %s \n", j.getAllowanceName(), j.getAllowanceProdAmount()+"", j.getAllowanceMealAmount()+"", j.getSum()+"");
            });

        });
    }
}