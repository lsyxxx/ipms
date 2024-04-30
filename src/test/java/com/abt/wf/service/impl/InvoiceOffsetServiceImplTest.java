package com.abt.wf.service.impl;

import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import com.abt.wf.service.InvoiceOffsetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InvoiceOffsetServiceImplTest {

    @Autowired
    private InvoiceOffsetService invoiceOffsetService;

    @Test
    void findDone() {
        InvoiceOffsetRequestForm form = new InvoiceOffsetRequestForm();
        form.setContractName("123");
        form.setQueryMode(1);
        form.setProcDefKey("rbsInvOffset");
        final List<InvoiceOffset> done = invoiceOffsetService.findTodo(form);
        assertNotNull(done);
        done.forEach(i -> {
            System.out.println(i.toString());
        });
    }


    @Test
    void find() {
    }
}