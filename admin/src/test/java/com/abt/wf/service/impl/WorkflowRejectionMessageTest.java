package com.abt.wf.service.impl;

import com.abt.common.model.User;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.service.SystemMessageService;
import com.abt.sys.service.UserService;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.repository.PurchaseApplyMainRepository;
import com.abt.wf.service.FlowOperationLogService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.abt.wf.config.Constants.*;
import static com.abt.wf.config.WorkFlowConfig.DEF_KEY_PURCHASE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** 验证共用审批处理中的拒绝消息触发条件。 */
class WorkflowRejectionMessageTest {

    private final RuntimeService runtimeService = mock(RuntimeService.class);
    private final TaskService taskService = mock(TaskService.class);
    private final FlowOperationLogService flowLogService = mock(FlowOperationLogService.class);
    private final UserService userService = mock(UserService.class);
    private final SystemMessageService messageService = mock(SystemMessageService.class);
    private final PurchaseApplyMainRepository repository = mock(PurchaseApplyMainRepository.class);
    private final PurchaseServiceImpl purchaseService = new PurchaseServiceImpl(null, null, runtimeService,
            taskService, flowLogService, userService, null, null, null, null, repository);
    private final PurchaseApplyMain form = new PurchaseApplyMain();
    private final Task task = mock(Task.class);

    @BeforeEach
    void setUp() {
        purchaseService.setSystemMessageService(messageService);
        form.setId("PUR-1");
        form.setProcessInstanceId("process-1");
        form.setProcessDefinitionKey(DEF_KEY_PURCHASE);
        form.setSubmitUserid("approver");
        form.setSubmitUsername("财务审批人");
        when(task.getId()).thenReturn("task-1");
        when(task.getProcessInstanceId()).thenReturn("process-1");
        when(task.getCreateTime()).thenReturn(new Date());
    }

    @Test
    void sendsRejectionToProcessStarterAfterRecordingTheResult() {
        form.setDecision(DECISION_REJECT);
        when(runtimeService.getVariables("process-1", List.of(KEY_STARTER, KEY_SERVICE)))
                .thenReturn(Map.of(KEY_STARTER, "starter", KEY_SERVICE, SERVICE_PURCHASE));
        User starter = new User("starter");
        starter.setUsername("申请人");
        when(userService.getSimpleUserInfo("starter")).thenReturn(starter);
        SystemMessage message = new SystemMessage();
        when(messageService.createSystemMessage("starter", "申请人", "",
                "您的%s流程(单据编号:PUR-1)已由财务审批人审批拒绝".formatted(SERVICE_PURCHASE), DEF_KEY_PURCHASE))
                .thenReturn(message);

        purchaseService.rejectHandler(form, task);

        InOrder order = inOrder(runtimeService, repository, flowLogService, messageService);
        order.verify(runtimeService).getVariables("process-1", List.of(KEY_STARTER, KEY_SERVICE));
        order.verify(runtimeService).deleteProcessInstance(eq("process-1"), startsWith(DELETE_REASON_REJECT));
        order.verify(repository).save(form);
        order.verify(flowLogService).saveLog(argThat(log -> STATE_DETAIL_REJECT.equals(log.getTaskResult())));
        order.verify(messageService).sendMessage(message);
        assertEquals(STATE_DETAIL_REJECT, form.getBusinessState());
        assertEquals("PUR-1", message.getEntityId());
        assertEquals(SystemMessage.MSG_RESULT_REJECT, message.getMsgResult());
        verify(taskService, never()).complete("task-1");
    }

    @Test
    void doesNotSendRejectionWhenApprovalPassesOrIsRevoked() {
        form.setDecision(DECISION_PASS);
        purchaseService.passHandler(form, task);
        purchaseService.doRevoke(form);

        verify(taskService).complete("task-1");
        verify(runtimeService).deleteProcessInstance("process-1", DEL_REASON_REVOKE);
        verifyNoInteractions(messageService);
    }
}
