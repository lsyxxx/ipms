package com.abt.wf.serivce.impl;

import com.abt.wf.entity.Reimburse;
import com.abt.wf.exception.RequiredParameterException;
import com.abt.wf.model.ReimburseApplyForm;
import com.abt.wf.serivce.ReimburseService;
import com.abt.wf.serivce.WorkFlowExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
@Slf4j
public class WorkFlowExecutionServiceImpl implements WorkFlowExecutionService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;

    private final ReimburseService reimburseService;

    private final IdentityService identityService;

    public static final String VARS_STARTER = "starter";


    public WorkFlowExecutionServiceImpl(RuntimeService runtimeService, TaskService taskService, HistoryService historyService, ReimburseService reimburseService, IdentityService identityService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
        this.reimburseService = reimburseService;
        this.identityService = identityService;
    }

    /**
     * 预览流程图key
     * @param username 用户名
     * @return
     */
    public String previewBusinessKey(String username, String procDefId) {
        return "PREVIEW_" + username + "_" + procDefId;
    }

    public static final String DELETE_PREVIEW_REASON = "AUTO_DELETE_PREVIEW_";

    /**
     * 预览流程图
     */
    @Override
    public List<HistoricTaskInstance> previewFlow(ReimburseApplyForm form) {
        log.info("预览流程图...previewProcessInstanceId: {}", form.getPreviewInstanceId());
        if (StringUtils.isNotBlank(form.getPreviewInstanceId())) {
            try {
                //已有预览流程图则删除
                runtimeService.deleteProcessInstance(form.getPreviewInstanceId(), DELETE_PREVIEW_REASON);
            } catch (Exception e) {
                log.error("删除预览流程失败! - ", e);
            }
        }
        Map<String, Object> vars = form.variableMap();
        final ProcessInstance previewInstance = runtimeService.startProcessInstanceByKey(form.getProcessDefinitionKey(), previewBusinessKey(form.getUsername(), form.getProcessDefinitionId()), vars);
        String procId = previewInstance.getId();
        Task task = taskService.createTaskQuery().active().processInstanceId(procId).singleResult();
        while(task != null) {
            taskService.complete(task.getId(), vars);
            task = taskService.createTaskQuery().processInstanceId(procId).active().singleResult();
        }

        final List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processInstanceId(procId).finished().orderByHistoricActivityInstanceStartTime().asc().list();
        return list;
    }

    public String userApplyBusinessKey(String username) {
        return "USER_APPLY_" + username;
    }


    /**
     * 申请
     *
     * @param form 申请表单
     * @return 业务实体
     */
    @Override
    @Transactional
    public Reimburse apply(ReimburseApplyForm form) {
        ensureProcessDefinitionId(form);
        setAuthUser(form.getUserid());
        String procDefId = form.getProcessDefinitionId();
        Map<String, Object> vars = form.variableMap();
        vars.put("starter", form.getUserid());
        final ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, userApplyBusinessKey(form.getUsername()), vars);
        final Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();
        taskService.setAssignee(task.getId(), form.getUserid());
        taskService.complete(task.getId(), vars);

        form.setProcessInstanceId(processInstance.getId());

        Reimburse reimburse = reimburseService.saveEntity(form);
        clearAuthUser();
        return reimburse;
    }

    public static final String DELETE_REASON_REJECT_BY = "Reject_by_ ";

    public String deleteReasonReject(String userid) {
        return DELETE_REASON_REJECT_BY + userid;
    }

    public void setAuthUser(String userid) {
        identityService.setAuthenticatedUserId(userid);
    }

    public void clearAuthUser() {
        identityService.clearAuthentication();
    }


    @Override
    public void approve(ReimburseApplyForm form) {
        ensureProcessId(form);
        setAuthUser(form.getUserid());
        String procId = form.getProcessInstanceId();
        final Task task = taskService.createTaskQuery().processInstanceId(procId).active().singleResult();
        taskService.claim(task.getId(), form.getUserid());
        taskService.setDescription(task.getId(), form.getDecision());
        if (StringUtils.isNotBlank(form.getComment())) {
            taskService.createComment(task.getId(), form.getProcessInstanceId(), form.getComment());
        }

        if (form.isReject()) {
            runtimeService.deleteProcessInstance(form.getProcessInstanceId(), deleteReasonReject(form.getUserid()));
        } else {
            //pass
            taskService.complete(task.getId());
        }

        clearAuthUser();
    }

    public static void ensureProcessId(ReimburseApplyForm form) {
        if (StringUtils.isNotBlank(form.getProcessInstanceId())) {
            return;
        }
        throw new RequiredParameterException("ProcessInstanceId(流程实例id)");
    }

    public static void ensureProcessDefinitionId(ReimburseApplyForm form) {
        if (StringUtils.isNotBlank(form.getProcessDefinitionId())) {
            return;
        }
        throw new RequiredParameterException("ProcessDefinitionId(流程定义id)");
    }



}
