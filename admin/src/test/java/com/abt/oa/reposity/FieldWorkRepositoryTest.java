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

import java.time.LocalDate;
import java.util.List;

import static com.abt.oa.OAConstants.FW_PASS;
import static com.abt.oa.OAConstants.FW_REJECT;
import static com.abt.oa.OAConstants.FW_WAITING;
import static com.abt.oa.OAConstants.FW_WITHDRAW;
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
        final Page<FieldWork> page = fieldWorkRepository.findTodoByQuery("流体", "U20230406006", null, null, null, pageable);
        final List<String> ids = page.getContent().stream().map(FieldWork::getId).toList();
        final List<FieldWork> records = ids.isEmpty() ? List.of() : fieldWorkRepository.findAllWithItemsByIdIn(ids);
        System.out.printf("list size = %d\n", records.size());
        records.forEach(i -> {
            System.out.printf("|-main: name: %s, date:%s, ids: %s \n", i.getUsername(), i.getAttendanceDate().toString(), i.getItemIds());
            i.getItems().forEach(j -> {
                System.out.printf("|----item: name: %s, prod: %s. meal: %s, sum: %s \n", j.getAllowanceName(), j.getAllowanceProdAmount()+"", j.getAllowanceMealAmount()+"", j.getSum()+"");
            });

        });
    }

    @Test
    void findAllByQueryWithAllStates() {
        Pageable pageable = PageRequest.of(0, 10);
        final Page<FieldWork> allPage = fieldWorkRepository.findAllByQuery(
                null, null, true, List.of("__none__"), null, null, pageable);
        final Page<FieldWork> allStatesPage = fieldWorkRepository.findAllByQuery(
                null, null, false, List.of(FW_PASS, FW_REJECT, FW_WAITING, FW_WITHDRAW), null, null, pageable);

        assertEquals(allPage.getTotalElements(), allStatesPage.getTotalElements());
        assertEquals(allPage.getContent().size(), allStatesPage.getContent().size());
    }

    @Test
    void findRecordsByUserInfo() {
        final List<FieldWork> records = fieldWorkRepository.findRecordsByUserInfo(null, null, "'A','G'", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 9, 9));
        assertNotNull(records);
        System.out.println("size: " + records.size());
    }
}
