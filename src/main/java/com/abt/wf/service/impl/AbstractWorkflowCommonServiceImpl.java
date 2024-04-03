package com.abt.wf.service.impl;

import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.WorkFlowService;
import com.abt.wf.util.WorkFlowUtil;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;

import java.util.List;

/**
 *
 */
@AllArgsConstructor
public abstract class AbstractWorkflowCommonServiceImpl<T extends WorkflowBase> implements WorkFlowService<T> {

    private IdentityService identityService;
    private FlowOperationLogService flowOperationLogService;
    private TaskService taskService;

    @Override
    public void setAuthUser(String userid) {
        identityService.setAuthenticatedUserId(userid);
    }

    @Override
    public List<FlowOperationLog> getCompletedOperationLogByEntityId(String entityId) {
        return flowOperationLogService.findLogsByEntityId(entityId);
    }

    @Override
    public void clearAuthUser() {
        identityService.clearAuthentication();
    }

    @Override
    public void approve(T form) {
        String decision = getDecision(form);
        beforeApprove(form, form.getSubmitUserid(), decision);
        if (WorkFlowUtil.isPass(decision)) {
            passHandler(form);
        } else if (WorkFlowUtil.isReject(decision)) {
            rejectHandler(form);
        } else {
            throw new BusinessException("审批结果只能是pass/reject，实际传入: " + decision);
        }

        afterApprove(form);
        clearAuthUser();
    }

    @Override
    public void beforeApprove(T baseForm, String authUser, String decision) {
        //validate
        WorkFlowUtil.ensureProcessId(baseForm);
        ensureEntityId(baseForm);
        WorkFlowUtil.decisionTranslate(decision);
        setAuthUser(authUser);
        String procId = baseForm.getProcessInstanceId();
        Task task = taskService.createTaskQuery().processInstanceId(procId).active().singleResult();
        //验证用户是否是审批用户
        baseForm.setCurrentTaskAssigneeId(task.getAssignee());
        this.isApproveUser(baseForm);

        //currentTask
        baseForm.setCurrentTaskDefId(task.getTaskDefinitionKey());
        baseForm.setCurrentTaskName(task.getName());
        baseForm.setCurrentTaskId(task.getId());
        baseForm.setCurrentTaskStartTime(TimeUtil.from(task.getCreateTime()));

    }

    @Override
    public boolean isApproveUser(T form) {
        if (!TokenUtil.getUseridFromAuthToken().equals(form.getCurrentTaskAssigneeId())) {
            throw new BusinessException("登录用户(" + form.getSubmitUsername() + ")不是当前审批用户!不能审批");
        }
        return true;
    }

    abstract String getDecision(T form);

    /**
     * 审批通过后操作
     */
    abstract void passHandler(T form);
    abstract void rejectHandler(T form);
    abstract void afterApprove(T form);



}
