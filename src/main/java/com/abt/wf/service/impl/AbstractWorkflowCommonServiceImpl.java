package com.abt.wf.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.model.User;
import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.TripReimburseForm;
import com.abt.wf.model.TripRequestForm;
import com.abt.wf.model.UserTaskDTO;
import com.abt.wf.model.ValidationResult;
import com.abt.wf.service.BusinessQueryService;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.WorkFlowService;
import com.abt.wf.util.WorkFlowUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.StartEventImpl;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.abt.wf.config.Constants.STATE_DETAIL_ACTIVE;

/**
 *
 */
@AllArgsConstructor
@Slf4j
public abstract class AbstractWorkflowCommonServiceImpl<T extends WorkflowBase> implements WorkFlowService<T> {

    private IdentityService identityService;
    private FlowOperationLogService flowOperationLogService;
    private TaskService taskService;
    private UserService userService;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;

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

    public void ensureProperty(String prop, String msg) {
        if (StringUtils.isBlank(prop)) {
            throw new MissingRequiredParameterException(msg);
        }
    }

    public Task startProcessAndApply(T form, Map<String, Object> variableMap, String serviceName) {
        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(form.getProcessDefinitionKey()).latestVersion().active().singleResult();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(form.getProcessDefinitionKey(), businessKey(form), variableMap);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();
        task.setAssignee(form.getCreateUserid());
        taskService.setAssignee(task.getId(), form.getCreateUserid());
        taskService.complete(task.getId());

        form.setProcessDefinitionId(processDefinition.getId());
        form.setProcessInstanceId(processInstance.getId());
        form.setProcessState(HistoricProcessInstance.STATE_ACTIVE);
        form.setBusinessState(STATE_DETAIL_ACTIVE);
        form.setServiceName(serviceName);
        return task;
    }

    /**
     * 预览
     * @param form 提交表单
     * @param vars 表单流程必须参数
     * @param bpmnModelInstance bpmn模型
     * @param copyList 抄送人
     */
    public List<UserTaskDTO> commonPreview(T form, Map<String, Object> vars, BpmnModelInstance bpmnModelInstance, List<String> copyList) {
        //验证必要参数
        final ValidationResult validationResult = beforePreview(form);
        if (!validationResult.isPass()) {
            throw new BusinessException(validationResult.getDescription());
        }

        final Collection<StartEvent> startEvents = bpmnModelInstance.getModelElementsByType(StartEvent.class);
        StartEventImpl startEvent = (StartEventImpl) startEvents.iterator().next();
        List<FlowNode> previewList = new ArrayList<>();
        WorkFlowUtil.findActivityNodes(startEvent, previewList, vars);
        //去掉startEvent和endEvent
        previewList = previewList.stream().filter(i -> !(i instanceof StartEvent || i instanceof EndEvent)).collect(Collectors.toList());
        List<UserTaskDTO> returnList = new ArrayList<>();
        //生成返回给前端的对象
        for (FlowNode node : previewList) {
            UserTaskDTO parent = flowNodeWrapper(node, form, bpmnModelInstance);
            returnList.add(parent);
        }
        if (copyList.isEmpty()) {
            return returnList;
        }
        //抄送节点
        UserTaskDTO cc = new UserTaskDTO();
        cc.setTaskType(Constants.TASK_TYPE_COPY);
        cc.setTaskName(Constants.TASK_NAME_COPY);
        cc.setSelectUserType(Constants.SELECT_USER_TYPE_MANUAL);
        for (String s : copyList) {
            UserTaskDTO dto = new UserTaskDTO();
            dto.setOperatorId(s);
            final User user = userService.getSimpleUserInfo(s);
            dto.setOperatorName(user.getUsername());
            cc.addUserTaskDTO(dto);
        }
        returnList.add(cc);
        return returnList;
    }

    /**
     * 将flowNode包装为UserTaskDTO
     */
    public UserTaskDTO flowNodeWrapper(FlowNode node, WorkflowBase form, BpmnModelInstance bpmnModelInstance) {
        UserTaskDTO parent = new UserTaskDTO();
        UserTaskDTO child = new UserTaskDTO();
        parent.setProcessDefinitionKey(form.getProcessDefinitionKey());
        parent.setProcessDefinitionId(form.getProcessDefinitionId());
        parent.setTaskDefinitionKey(node.getId());
        parent.setTaskName(node.getName());
        child.setProcessDefinitionKey(form.getProcessDefinitionKey());
        child.setProcessDefinitionId(form.getProcessDefinitionId());
        if (node instanceof UserTask u) {
            final Collection<CamundaProperty> extensionProperties = WorkFlowUtil.queryUserTaskBpmnModelExtensionProperties(bpmnModelInstance, node.getId());
            parent.setProperties(extensionProperties);
            parent.setPreview(true);
            if (parent.isApplyNode()) {
                child.setOperatorId(form.getSubmitUserid());
                child.setOperatorName(form.getSubmitUsername());
            } else {
                String assigneeId = u.getCamundaAssignee();
                //指定用户才能解析
                if (parent.isSpecific()) {
                    final User simpleUserInfo = userService.getSimpleUserInfo(new User(assigneeId));
                    if (simpleUserInfo != null) {
                        child.setOperatorId(assigneeId);
                        child.setOperatorName(simpleUserInfo.getUsername());
                    } else {
                        log.warn("未查询到用户{}", assigneeId);
                    }
                }
            }
        }
        parent.addUserTaskDTO(child);
        return parent;
    }

    @Override
    public List<FlowOperationLog> processRecord(String entityId, String serviceName) {
        List<FlowOperationLog> completed = getCompletedOperationLogByEntityId(entityId);
        if (completed.isEmpty()) {
            return completed;
        }
        String procId = completed.get(0).getProcessInstanceId();
        final Task task = taskService.createTaskQuery().active().processInstanceId(procId).singleResult();
        if (task == null) {
            return completed;
        }
        FlowOperationLog active = new FlowOperationLog();
        active.setEntityId(entityId);
        active.setServiceName(serviceName);
        active.setTaskDefinitionKey(task.getTaskDefinitionKey());
        active.setTaskName(task.getName());
        active.setTaskStartTime(TimeUtil.from(task.getCreateTime()));
        active.setOperatorId(task.getAssignee());
        User operator = userService.getSimpleUserInfo(task.getAssignee());
        if (operator != null) {
            active.setOperatorName(operator.getUsername());
        }
        active.setTaskResult(STATE_DETAIL_ACTIVE);
        completed.add(active);

        return completed;
    }

    /**
     * 删除校验
     * @param entityId 业务实体id
     */
    public ValidationResult deleteValidate(String entityId, String userId) {
        //删除权限
        //1. 获取当前用户
        //2. 查询当前用户是否有删除权限
        //角色包含《流程管理》权限，查询Relevance表:SELECT * FROM [dbo].[Relevance] where  1=1 and SecondId = 'JS002' and [Key] = 'UserRole'
        //key是角色模块，SecondId是角色id, FirstId是对应用户
        if (!"System".equals(userId)) {
            //超级管理员
            ValidationResult.fail("只有管理员可以删除! 请联系管理员");
        }

        return ValidationResult.pass();
    }




    /**
     * 预览前验证
     * @param form 表单
     */
    abstract ValidationResult beforePreview(T form);


    abstract String getDecision(T form);

    /**
     * 审批通过后操作
     */
    abstract void passHandler(T form);
    abstract void rejectHandler(T form);
    abstract void afterApprove(T form);



}
