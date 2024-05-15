package com.abt.wf.service.impl;

import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import com.abt.wf.service.InvoiceOffsetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.abt.wf.TestConst.USER_ID;
import static com.abt.wf.TestConst.USER_NAME;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InvoiceOffsetServiceImplTest {

    @Autowired
    private InvoiceOffsetService invoiceOffsetService;

    @Test
    void findTodo() {
        InvoiceOffsetRequestForm form = new InvoiceOffsetRequestForm();
//        form.setContractName("22");
//        form.setQueryMode(1);
//        form.setProcDefKey("rbsInvOffset");
        form.setLimit(20);
        form.setUserid(USER_ID);
        form.setUsername(USER_NAME);
        final List<InvoiceOffset> todo = invoiceOffsetService.findMyTodoByCriteria(form);
        assertNotNull(todo);
        System.out.println(todo.size());
        todo.forEach(i -> System.out.println(i.getContractName()));
    }

    @Test
    void findDone() {
        InvoiceOffsetRequestForm form = new InvoiceOffsetRequestForm();
//        form.setContractName("22");
//        form.setQueryMode(1);
//        form.setProcDefKey("rbsInvOffset");
        form.setLimit(20);
        form.setUserid(USER_ID);
        form.setUsername(USER_NAME);
        final List<InvoiceOffset> done = invoiceOffsetService.findMyDoneByCriteriaPageable(form);
        assertNotNull(done);
        System.out.println(done.size());
        done.forEach(i -> System.out.println(i.getContractName()));

    }

    @Test
    void findAll() {
        InvoiceOffsetRequestForm form = new InvoiceOffsetRequestForm();
//        form.setContractName("22");
//        form.setQueryMode(1);
//        form.setProcDefKey("rbsInvOffset");
        form.setLimit(20);
        form.setUserid("USER_ID");
//        form.setUsername(USER_NAME);
        final Page<InvoiceOffset> all = invoiceOffsetService.findAllByCriteria(form);
        System.out.println(all.getTotalElements());
        all.getContent().forEach(i -> System.out.println(i.toString()));
    }


    @Test
    void find() {
        final InvoiceOffset load = invoiceOffsetService.load("202405061714967471225");
        assertNotNull(load);
//        assertNotNull(load.getCurrentTask());
//        System.out.println(load.getCurrentTask().toString());
    }
}