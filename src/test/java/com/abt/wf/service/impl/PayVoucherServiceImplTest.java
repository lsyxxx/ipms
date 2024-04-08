package com.abt.wf.service.impl;

import com.abt.wf.entity.PayVoucher;
import com.abt.wf.model.PayVoucherRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.service.PayVoucherService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
class PayVoucherServiceImplTest {

    @Autowired
    private PayVoucherService payVoucherService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void apply() {
        PayVoucher payVoucher = new PayVoucher();
        payVoucher.setProject("测试1111");
        payVoucher.setPayInvoiceNum(3);
        payVoucher.setPayAmount(new BigDecimal(2000));
        payVoucher.setPayDesc("付款内容");
        payVoucher.setPayedAmount(new BigDecimal("20.00"));
        payVoucher.setContractNo("合同编号1231111");
        payVoucher.setContractName("合同名称000011111");
        payVoucher.setContractDesc("合同内容描述");
        payVoucher.setContractAmount(new BigDecimal("11120.00"));
        payVoucher.setProcessDefinitionId("rbsPay:5:5b95a811-f54f-11ee-86d9-a497b12f53fd");
        payVoucher.setProcessDefinitionKey("rbsPay");
        payVoucher.setManagers("abt112,abt002");
        payVoucherService.apply(payVoucher);
    }

    @Test
    void testPreview() {
        PayVoucher payVoucher = new PayVoucher();
        payVoucher.setPayAmount(new BigDecimal(2000));
        final List<UserTaskDTO> preview = payVoucherService.preview(payVoucher);
        Assert.notEmpty(preview, "empty");
        preview.forEach(i -> System.out.printf(i.toString()));
    }

    @Test
    void testApprove() {
        PayVoucher payVoucher = new PayVoucher();
        payVoucher.setSubmitUserid("abt112");
        payVoucher.setSubmitUsername("刘宋菀");
        payVoucher.setComment("abt112评论");
        payVoucher.setDecision("pass");
        payVoucher.setId("202404081712543257176");
        payVoucher.setProcessInstanceId("90600f25-f54f-11ee-8ef8-a497b12f53fd");
        payVoucher.setProcessDefinitionKey("rbsPay");

        payVoucherService.approve(payVoucher);
    }

    @Test
    void testFindAll() {
        PayVoucherRequestForm requestForm = new PayVoucherRequestForm();
        final List<PayVoucher> all = payVoucherService.findAllByCriteriaPageable(requestForm);
        Assert.notEmpty(all, "empty");
        all.forEach(i -> System.out.printf(i.toString()));

    }


}