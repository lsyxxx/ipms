package com.abt.wf.repository;

import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.model.act.ActHiTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InvoiceOffsetRepositoryTest {

    @Autowired
    private InvoiceOffsetRepository invoiceOffsetRepository;

    @Test
    void find() {
        invoiceOffsetRepository.findById("d38dd554-2a37-4354-b324-2d573bd379f0").ifPresent(i -> {
//            assertNotNull(i.getInvokedTask());
//            for (ActHiTaskInstance task : i.getInvokedTask()) {
//                System.out.println(task.toString());
//            }

            assertNotNull(i.getCurrentTask());
            System.out.println("----currentTask----");
            System.out.println(i.getCurrentTask().toString());

            assertNotNull(i.getProcInstance());

            System.out.println("-------procInstance----");
            System.out.println(i.getProcInstance().toString());

//            System.out.println("-------hiTaskInstance----");
//            System.out.println(i.getInvokedTask().toString());

//            assertNotNull(i.getInvokedTask());
//            System.out.println("----invokedTask----");
//            i.getInvokedTask().forEach(j -> {
//                System.out.println(j.toString());
//            });

        });
    }

    @Test
    void create() {
        InvoiceOffset form = new InvoiceOffset();
        form.setCompany("阿伯塔");
        form.setInvoiceCode("invCode123");
        form.setContractAmount(1000.00);
        form.setContractName("合同名称123");
        form.setInvoiceAmount(100.00);
        form.setSupplierName("供应商123");
        form.setProcessInstanceId("d6c580a2-0607-11ef-9728-a497b12f53fd");
        form.setProcessDefinitionId("rbsInvOffset:2:ac6da89c-0607-11ef-9728-a497b12f53fd");
        invoiceOffsetRepository.save(form);
    }




}