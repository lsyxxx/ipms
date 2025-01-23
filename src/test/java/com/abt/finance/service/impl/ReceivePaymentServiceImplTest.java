package com.abt.finance.service.impl;

import com.abt.finance.entity.ReceivePayment;
import com.abt.finance.model.ReceivePaymentRequestForm;
import com.abt.finance.service.ReceivePaymentService;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.model.InvoiceApplyStat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReceivePaymentServiceImplTest {

    @Autowired
    private ReceivePaymentService receivePaymentService;

    @Test
    void findByNotifyUsers() {
        ReceivePaymentRequestForm form = new ReceivePaymentRequestForm();
        form.setLimit(40);
        form.setPage(1);
        form.setUserid("621faa40-f45c-4da8-9a8f-65b0c5353f40");
        final Page<ReceivePayment> page = receivePaymentService.findByNotifyUsers(form);
        assertNotNull(page);
        System.out.println(page.getTotalElements());
        System.out.println(page.getContent().size());

        page.getContent().forEach(i -> {
            System.out.printf("amount: %s, customer: %s\n", i.getAmount().toString(), i.getCustomerName());
        });

    }

    @Test
    void receivePaymentStats() {
        ReceivePaymentRequestForm form = new ReceivePaymentRequestForm();
        form.setLimit(40);
        form.setPage(1);
//        form.setStatsType(List.of("project", "clientId", "clientName", "company"));
        final Page<InvoiceApplyStat> page = receivePaymentService.payingStats(form);
        assertNotNull(page);
        System.out.printf("total: %d\n", page.getTotalElements());
        System.out.printf("current: page: %d, size: %d\n", page.getNumber(), page.getContent().size());
        page.getContent().forEach(this::print);
    }

    void print(InvoiceApplyStat stat) {
        System.out.printf("project: %s, clientName: %s, clientId: %s, contractName: %s, testNo: %s, company%s\n",
                stat.getProject(), stat.getClientName(), stat.getClientId(), stat.getContractName(), stat.getTestNo(), stat.getCompany());
    }


    @Test
    void payingStats() {
        ReceivePaymentRequestForm form = new ReceivePaymentRequestForm();
        form.setLimit(40);
        form.setPage(1);
        form.setStatsType(List.of("project", "clientId", "clientName"));

    }
}