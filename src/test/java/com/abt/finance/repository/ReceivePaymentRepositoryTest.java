package com.abt.finance.repository;

import com.abt.finance.entity.ReceivePayment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ReceivePaymentRepositoryTest {

    @Autowired
    private ReceivePaymentRepository receivePaymentRepository;

    @Test
    void findWithReferenceById() {
    }

    @Test
    void findByQuery() {
    }

    @Test
    void findWithInvoice() {
        final List<ReceivePayment> list = receivePaymentRepository.findWithInvoice();
        Assertions.assertNotNull(list);
        list.forEach(p -> {
            System.out.println(p.getInvoices().size());
        });
    }
}