package com.abt.finance.service.impl;

import com.abt.finance.entity.CreditBook;
import com.abt.finance.service.CreditBookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreditBookServiceImplTest {

    @Autowired
    private CreditBookService creditBookService;

    @Test
    void saveCreditBook() {
        CreditBook book = new CreditBook();
        book.setReceiveUser("recuser1");
        book.setServiceName("费用报销");
        book.setCompany("ABT");
        book.setReason("reasonsfafdsf");
        book.setPayLevel("加急");
        book.setBusinessId("entity123");
        book.setCost(125.0);
        creditBookService.saveCreditBook(book);

    }
}