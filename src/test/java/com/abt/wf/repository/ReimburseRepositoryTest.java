package com.abt.wf.repository;

import com.abt.wf.entity.Reimburse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReimburseRepositoryTest {

    @Autowired
    private ReimburseRepository reimburseRepository;

    @Test
    void findTodoList() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createDate"));
        final Page<Reimburse> page = reimburseRepository.findMyTodoPaged("621faa40-f45c-4da8-9a8f-65b0c5353f40", "1",
                LocalDateTime.of(2024, 6, 12, 0, 0), null,
                pageable);
        assertNotNull(page);
        System.out.printf("总条数:%d, 总页数: %d%n", page.getTotalElements(), page.getTotalPages());
        page.getContent().forEach(i -> {
            System.out.printf("entityId:%s, reason:%s, cost:%s,procId: %s, curAssignee:%s, curAsName:%s%n", i.getId(), i.getReason(), i.getCost(), i.getProcessInstanceId(), i.getCurrentTask().getAssignee(), i.getCurrentTask().getTuser().getName());
        });
    }

    @Test
    void findDone() {
//        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createDate"));
//        final Page<Reimburse> page = reimburseRepository.findMyDonePaged("621faa40-f45c-4da8-9a8f-65b0c5353f40", "1",
//                LocalDateTime.of(2024, 6, 12, 0, 0), null,
//                pageable);
//        assertNotNull(page);
//        System.out.printf("总条数:%d, 总页数: %d%n", page.getTotalElements(), page.getTotalPages());
//        page.getContent().forEach(i -> {
//            System.out.printf("entityId:%s, reason:%s, cost:%s,procId: %s, curAssignee:%s, curAsName:%s%n", i.getId(), i.getReason(), i.getCost(), i.getProcessInstanceId(), i.getCurrentTask().getAssignee(), i.getCurrentTask().getTuser().getName());
//        });
    }
}