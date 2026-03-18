package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceOffset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InvoiceOffsetRepositoryTest {
    @Autowired
    private InvoiceOffsetRepository invoiceOffsetRepository;

    private Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createDate"));

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findUserApplyByQueryPaged() {
        final Page<InvoiceOffset> page = invoiceOffsetRepository.findUserApplyByQueryPaged(null, null, null, null, null, pageable);
        assertNotNull(page);
        System.out.printf("findMyApplyByQueryPaged总条数:%d, 总页数: %d%n", page.getTotalElements(), page.getTotalPages());
        page.getContent().forEach(i -> {
            System.out.printf("id:%s,createUsername:%s,contractName:%s,clientName:%s%n", i.getId(), i.getCreateUsername(),i.getContractName(), i.getSupplierName());
        });
    }

    private void printPage(Page<InvoiceOffset> page) {
        System.out.printf("总条数:%d, 总页数: %d%n", page.getTotalElements(), page.getTotalPages());
        page.getContent().forEach(i -> {
            System.out.printf("id:%s,createUsername:%s,contractName:%s,clientName:%s%n", i.getId(), i.getCreateUsername(),i.getContractName(), i.getSupplierName());
        });
    }

    @Test
    void findUserDoneByQueryPaged() {
        final Page<InvoiceOffset> page = invoiceOffsetRepository.findUserDoneByQueryPaged("U20230406013", "北京", null, null, null, pageable);
        printPage(page);
    }

    @Test
    void findUserTodoByQueryPaged() {
        final Page<InvoiceOffset> page = invoiceOffsetRepository.findUserTodoByQueryPaged("U20230406013", "", null, null, null, pageable);
        printPage(page);
    }

    @Test
    void findAllByQueryPaged() {
        final Page<InvoiceOffset> page = invoiceOffsetRepository.findAllByQueryPaged("0613", "", null, null, pageable);
        printPage(page);
    }
}