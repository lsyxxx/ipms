package com.abt.wf.repository;

import com.abt.sys.exception.BusinessException;
import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.InvoiceOffsetRequestForm;
import com.abt.wf.model.act.ActHiTaskInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
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

//            assertNotNull(i.getCurrentTask());
//            System.out.println("----currentTask----");
//            System.out.println(i.getCurrentTask().toString());
//
//            assertNotNull(i.getProcInstance());
//
//            System.out.println("-------procInstance----");
//            System.out.println(i.getProcInstance().toString());

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
        form.setInvoiceCode("invCode222");
        form.setContractAmount(1000.00);
        form.setContractName("合同名称22");
        form.setInvoiceAmount(100.00);
        form.setSupplierName("供应商22");
        form.setProcessDefinitionKey("rbsInvOffset");
        form.setBusinessState("审批中");
        form.setProcessState("ACTIVE");
        form.setProcessInstanceId("a878241b-0b5b-11ef-8b7d-a497b12f53fd");
        form.setProcessDefinitionId("rbsInvOffset:1:e320d234-0b51-11ef-bb97-a497b12f53fd");
        invoiceOffsetRepository.save(form);
    }

    @Test
    void query() {
        final List<InvoiceOffset> all = invoiceOffsetRepository.findAll();
        assertNotNull(all);
        all.forEach(i -> System.out.println(i.toString()));
    }

    @Test
    void queryWithCurrentTask() {
        final InvoiceOffset invoiceOffset = invoiceOffsetRepository.findById("202405061714967471225").orElseThrow(() -> new BusinessException("未查询到"));
        assertNotNull(invoiceOffset);
        System.out.println(invoiceOffset.getProcInstance().toString());

    }


    @Test
    void findMyApply() {
        InvoiceOffsetRequestForm requestForm = new InvoiceOffsetRequestForm();
        requestForm.setLimit(20);
        Pageable page = PageRequest.of(requestForm.jpaPage(), requestForm.getLimit(),
                Sort.by(Sort.Order.desc("createDate")));
        final Page<InvoiceOffset> all = invoiceOffsetRepository.findAll(page);
        assertNotNull(all);
        System.out.println("----- 单页数量 ------");
        System.out.println(all.getContent().size());
        System.out.println("------总条数 -----------");
        System.out.println(all.getTotalElements());

    }




}