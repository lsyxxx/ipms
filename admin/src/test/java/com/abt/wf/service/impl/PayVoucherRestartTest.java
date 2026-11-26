package com.abt.wf.service.impl;

import com.abt.finance.service.ExpenseCategorySupport;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.entity.act.ActRuTask;
import com.abt.wf.repository.PayVoucherRepository;
import com.abt.wf.service.FlowOperationLogService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;

import static com.abt.wf.config.Constants.SERVICE_PAY;
import static com.abt.wf.config.Constants.VAR_KEY_ENTITY;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_PAY_VOUCHER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

/** 验证支付单重新提交时不会带入原流程的审批任务。 */
@ExtendWith(MockitoExtension.class)
class PayVoucherRestartTest {
    @Mock
    private PayVoucherRepository payVoucherRepository;
    @Mock
    private ExpenseCategorySupport expenseCategorySupport;
    @Mock
    private FlowOperationLogService flowOperationLogService;
    @Mock
    private RuntimeService runtimeService;

    @Spy
    @InjectMocks
    private PayVoucherServiceImpl service;

    @Test
    void copyingPaymentClearsPreviousTask() {
        PayVoucher original = paymentWithPreviousTask();
        original.setId("old-payment");
        doReturn(original).when(service).load("old-payment");

        PayVoucher copy = service.getCopyEntity("old-payment");

        assertNull(copy.getId());
        assertNull(copy.getProcessInstanceId());
        assertNull(copy.getCurrentTask());
        assertEquals(BigDecimal.TEN, copy.getPayAmount());
    }

    @Test
    void submittingOldFormClearsPreviousTaskBeforeSaving() {
        PayVoucher form = paymentWithPreviousTask();
        Task applyTask = mock(Task.class);
        when(applyTask.getCreateTime()).thenReturn(new Date());
        doAnswer(invocation -> {
            assertNull(form.getCurrentTask());
            form.setProcessInstanceId("new-process");
            return applyTask;
        }).when(service).startProcessAndApply(same(form), anyMap(), eq(SERVICE_PAY));
        when(payVoucherRepository.save(same(form))).thenAnswer(invocation -> {
            assertNull(form.getCurrentTask());
            form.setId("new-payment");
            return form;
        });

        PayVoucher saved = service.apply(form);

        assertEquals("new-payment", saved.getId());
        verify(runtimeService).setVariable("new-process", VAR_KEY_ENTITY, "new-payment");
    }

    private PayVoucher paymentWithPreviousTask() {
        PayVoucher form = new PayVoucher();
        form.setProcessDefinitionKey(DEF_KEY_PAY_VOUCHER);
        form.setProcessInstanceId("old-process");
        form.setPayAmount(BigDecimal.TEN);
        ActRuTask oldTask = new ActRuTask();
        oldTask.setId("old-task");
        oldTask.setProcInstId("old-process");
        form.setCurrentTask(oldTask);
        return form;
    }
}
