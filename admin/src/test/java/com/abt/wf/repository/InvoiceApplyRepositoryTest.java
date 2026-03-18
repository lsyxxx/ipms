package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceApply;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InvoiceApplyRepositoryTest {

    @Autowired
    private InvoiceApplyRepository invoiceApplyRepository;

    @Test
    void findMyApplyByQueryPaged() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createDate"));
        final Page<InvoiceApply> page = invoiceApplyRepository.findUserApplyByQueryPaged("张", null, null, null, null, pageable);
        assertNotNull(page);
        System.out.printf("findMyApplyByQueryPaged总条数:%d, 总页数: %d%n", page.getTotalElements(), page.getTotalPages());
        page.getContent().forEach(i -> {
            System.out.printf("id:%s,createUsername:%s,contractName:%s,clientName:%s%n", i.getId(), i.getCreateUsername(),i.getContractName(), i.getClientName());
        });
    }


    @Test
    void findUserTodoByQueryPaged() {
    }

    @Test
    void findUserDoneByQueryPaged() {


    }

    @Test
    void findAllByQueryPaged() {
    }
}