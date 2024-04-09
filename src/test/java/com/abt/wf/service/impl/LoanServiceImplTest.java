package com.abt.wf.service.impl;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.Loan;
import com.abt.wf.model.LoanRequestForm;
import com.abt.wf.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoanServiceImplTest {

    @Autowired
    private LoanService loanService;

    @Test
    void testApply() {
        Loan form = new Loan();
        form.setProcessDefinitionKey("rbsLoan");
        form.setProject("测试项目");
        form.setDeptId("4536fb5a-8263-4489-8c49-c86b3f67cba3");
        form.setDeptName("综合事务部");
        form.setLoanAmount(1000.00);
        form.setReason("测试测试");
        form.setReceiveUser("abt112");
        form.setReceiveBank("招行");
        form.setReceiveAccount("bankAccount12346");
        form.setPayType(Constants.LOAN_PAY_TYPE_ONLINE);

        loanService.apply(form);
    }

    @Test
    void testApprove() {
        Loan form = new Loan();
        form.setId("202404091712653877703");
        form.setProcessInstanceId("1eda975a-f651-11ee-9b27-a497b12f53fd");
        form.setDecision("pass");
        form.setComment("comment1231321");
        form.setSubmitUserid("abt112");
        form.setSubmitUsername("abt112");
        loanService.approve(form);
    }

    @Test
    void testFindAll() {
        LoanRequestForm form = new LoanRequestForm();
//        form.setUserid("abt112");
        form.setProject("ceshi ");
        final List<Loan> all = loanService.findAllByCriteriaPageable(form);
        assertNotNull(all);
        System.out.println(all.size());
        all.forEach(i -> System.out.println(i.toString()));
    }
}