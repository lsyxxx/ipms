package com.abt.wf.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.model.RequestForm;
import com.abt.common.model.User;
import com.abt.common.util.FileUtil;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.TimeUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.service.IFileService;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.entity.UserSignature;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.entity.act.ActRuTask;
import com.abt.wf.model.ReimburseExportDTO;
import com.abt.wf.model.UserTaskDTO;
import com.abt.common.model.ValidationResult;
import com.abt.wf.service.*;
import com.abt.wf.util.WorkFlowUtil;
import cn.idev.excel.EasyExcel;
import cn.idev.excel.metadata.data.WriteCellData;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
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
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static com.abt.common.ExcelUtil.*;
import static com.abt.oa.OAConstants.*;
import static com.abt.wf.config.Constants.*;

/**
 * 通用
 */
@AllArgsConstructor
@Slf4j
public abstract class AbstractWorkflowCommonServiceImpl<T extends WorkflowBase, R extends RequestForm> implements WorkFlowService<T>, BusinessService<R, T> {

    private IdentityService identityService;
    private FlowOperationLogService flowOperationLogService;
    private TaskService taskService;
    private UserService userService;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;
    private IFileService fileService;
    private HistoryService historyService;
    private SignatureService signatureService;

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
        final String entityId = getEntityId(form);
        final T entity = load(entityId);
        String decision = getDecision(form);
        //set
        this.setApprovalResult(form, entity);
        Task task = beforeApprove(entity, form.getSubmitUserid(), decision);
        taskService.setVariable(task.getId(), VAR_KEY_DECISION, decision);
        if (WorkFlowUtil.isPass(decision)) {
            passHandler(entity, task);
        } else if (WorkFlowUtil.isReject(decision)) {
            rejectHandler(entity, task);
        } else {
            throw new BusinessException("审批结果只能是pass/reject，实际传入: " + decision);
        }

        afterApprove(entity);
        clearAuthUser();
    }


    @Override
    public T apply(T form) {
        //-- validate
        WorkFlowUtil.ensureProcessDefinitionKey(form);
        validateAttachment(form);

        //-- prepare
        final Map<String, Object> variableMap = this.createVariableMap(form);

        //-- start instance
        final Task applyTask = this.startProcessAndApply(form, variableMap, businessKey(form));

        //-- save entity
        final T entity = this.saveEntity(form);
        final String id = this.getEntityId(entity);
        runtimeService.setVariable(form.getProcessInstanceId(), Constants.VAR_KEY_ENTITY, id);

        //-- record
        FlowOperationLog optLog = FlowOperationLog.applyLog(entity.getCreateUserid(), entity.getCreateUsername(), form, applyTask, id);
        optLog.setTaskDefinitionKey(applyTask.getTaskDefinitionKey());
        optLog.setTaskResult(STATE_DETAIL_APPLY);
        flowOperationLogService.saveLog(optLog);

        return entity;
    }

    @Override
    public Task beforeApprove(T baseForm, String authUser, String decision) {
        //validate
        ensureEntityId(baseForm);
        WorkFlowUtil.ensureProcessId(baseForm);
        WorkFlowUtil.decisionTranslate(decision);
        setAuthUser(authUser);
        String procId = baseForm.getProcessInstanceId();
        Task task = taskService.createTaskQuery().processInstanceId(procId).active().singleResult();
        //验证用户是否是审批用户
        baseForm.setCurrentTaskId(task.getId());
        //currentTask
        baseForm.setCurrentTaskDefId(task.getTaskDefinitionKey());
        baseForm.setCurrentTaskName(task.getName());
        baseForm.setCurrentTaskId(task.getId());
        baseForm.setCurrentTaskStartTime(TimeUtil.from(task.getCreateTime()));
        baseForm.setCurrentTaskAssigneeId(task.getAssignee());
        this.isApproveUser(baseForm);
        return task;
    }

    public boolean doIsApproveUser(T form) {
        if (!form.getSubmitUserid().equals(form.getCurrentTaskAssigneeId())) {
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
        //使用starter
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
     *
     * @param form              提交表单
     * @param vars              表单流程必须参数
     * @param bpmnModelInstance bpmn模型
     * @param copyList          抄送人
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
        previewList = previewList.stream().filter(i -> !(i instanceof StartEvent || i instanceof EndEvent)).toList();
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
//        UserTaskDTO cc = new UserTaskDTO();
//        cc.setTaskType(Constants.TASK_TYPE_COPY);
//        cc.setTaskName(Constants.TASK_NAME_COPY);
//        cc.setSelectUserType(Constants.SELECT_USER_TYPE_COPY);
//        for (String s : copyList) {
//            UserTaskDTO dto = new UserTaskDTO();
//            dto.setOperatorId(s);
//            dto.setTaskType(TASK_TYPE_COPY);
//            dto.setTaskName(TASK_NAME_COPY);
//            dto.setSelectUserType(SELECT_USER_TYPE_SPECIFIC);
//            final User user = userService.getSimpleUserInfo(s);
//            dto.setOperatorName(user.getUsername());
//            cc.addUserTaskDTO(dto);
//
//        }
//        returnList.add(cc);
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
            UserTask userTaskModel = (UserTask) u;
            StringBuilder desc = new StringBuilder();
            userTaskModel.getDocumentations().forEach(d -> {
                desc.append(d.getTextContent());
            });
            parent.setTaskDesc(desc.toString());
            final Collection<CamundaProperty> extensionProperties = WorkFlowUtil.queryUserTaskBpmnModelExtensionProperties(bpmnModelInstance, node.getId());
            if (extensionProperties != null) {
                parent.setProperties(extensionProperties);
            }
            parent.setPreview(true);
            if (parent.isApplyNode()) {
                child.setOperatorId(form.getSubmitUserid());
                child.setOperatorName(form.getSubmitUsername());
                parent.setAssignee(new User(form.getSubmitUserid(), form.getSubmitUsername()));
            } else if (parent.isSeqApprove()) {
                String assigneeId = u.getCamundaAssignee();
                //指定用户才能解析
                if (parent.isSpecific()) {
                    final User simpleUserInfo = userService.getSimpleUserInfo(new User(assigneeId));
                    if (simpleUserInfo != null) {
                        child.setOperatorId(assigneeId);
                        child.setOperatorName(simpleUserInfo.getUsername());
                        User nu = new User(assigneeId, simpleUserInfo.getUsername());
                        parent.setAssignee(nu);
                        parent.setAssigneeList(List.of(nu));
                    } else {
                        log.warn("未查询到用户{}", assigneeId);
                    }
                }
            } else if (parent.isOrApprove()) {
                //或签节点，查询候选人
                final List<User> candidateUsers = getCandidateUsers(bpmnModelInstance, node.getId());
                parent.setCandidateUsers(candidateUsers);
                parent.setAssigneeList(candidateUsers);

            } else {
                log.warn("流程节点未配置审批模式! -- taskDefKey: {}", parent.getTaskDefinitionKey());
            }
        }
        parent.addUserTaskDTO(child);
        return parent;
    }

    /**
     * 简单的审批记录，包含申请节点及当前正在审批中的节点
     * @param entityId 业务实体Id
     * @param serviceName 业务铭恒
     */
    public List<FlowOperationLog> simpleProcessRecord(String entityId, String serviceName, String procId) {
        List<FlowOperationLog> completed = getCompletedOperationLogByEntityId(entityId);
        //正在进行的
        if (!completed.isEmpty()) {
            setActiveTaskOptLog(completed, entityId, serviceName, procId);
        }
        return completed;
    }

    public void setActiveTaskOptLog(List<FlowOperationLog> logs, String entityId, String serviceName, String procId) {
//        String procId = logs.get(0).getProcessInstanceId();
        final Task task = taskService.createTaskQuery().active().processInstanceId(procId).singleResult();
        if (task != null) {
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
            logs.add(active);
        }
    }

    @Override
    public List<FlowOperationLog> processRecord(String entityId, String serviceName) {
        List<FlowOperationLog> completed = getCompletedOperationLogByEntityId(entityId);
        if (completed.isEmpty()) {
            return completed;
        }
        //添加签名
        for (FlowOperationLog optLog : completed) {
            if (optLog.getTaskName() != null && optLog.getTaskName().contains("出纳")) {
                //出纳不显示签名
                continue;
            }
            String userid = optLog.getOperatorId();
            String imgStr = getImageBase64String(userid);
            if (StringUtils.isBlank(imgStr)) {
                log.warn("未查询到用户({})签名!", userid);
            }
            optLog.setSignatureBase64(imgStr);
        }
        final T entity = load(entityId);
        setActiveTaskOptLog(completed, entityId, serviceName, entity.getProcessInstanceId());
        return completed;
    }

    public String getImageBase64String(String userid) {
        return signatureService.getUserSignatureBase64StringByUserid(userid);
    }

    public List<String> getCandidateUserStringList(BpmnModelInstance bpmnModelInstance, String taskDefId) {
        UserTask userTaskModel = bpmnModelInstance.getModelElementById(taskDefId);
        final String camundaCandidateUsers = userTaskModel.getCamundaCandidateUsers();
        if (StringUtils.isEmpty(camundaCandidateUsers)) {
            return new ArrayList<>();
        }
        return Arrays.asList(camundaCandidateUsers.split(","));
    }

    public List<User> userWrapper(List<String> userids) {
        if (userids == null) {
            return new ArrayList<>();
        }
        List<User> list = new ArrayList<>();
        userids.forEach(i -> {
            User user = new User(i);
            final User simpleUserInfo = userService.getSimpleUserInfo(new User(i));
            if (simpleUserInfo != null) {
                user.setUsername(simpleUserInfo.getUsername());
            }
            list.add(user);
        });
        return list;
    }

    public List<User> getCandidateUsers(BpmnModelInstance bpmnModelInstance, String taskDefId) {
        final List<String> candidateUserStringList = getCandidateUserStringList(bpmnModelInstance, taskDefId);
        return userWrapper(candidateUserStringList);
    }

    /**
     * 删除校验
     *
     * @param entityId 业务实体id
     */
    public ValidationResult deleteValidate(String entityId, String userId) {
        //删除权限
        //1. 获取当前用户
        //2. 查询当前用户是否有删除权限
        return ValidationResult.pass();
    }

    public void commonRejectHandler(T form, Task task, String comment, String id) {
        taskService.claim(task.getId(), form.getSubmitUserid());
//        taskService.complete(task.getId());
        //delete process
        runtimeService.setVariable(task.getProcessInstanceId(), VAR_KEY_REVOKE, DELETE_STATE_DELETE);
        runtimeService.deleteProcessInstance(task.getProcessInstanceId(), Constants.DELETE_REASON_REJECT + "_" + form.getSubmitUserid() + "_" + form.getSubmitUsername());
        //update status
        form.setBusinessState(STATE_DETAIL_REJECT);
        saveEntity(form);

        FlowOperationLog optLog = FlowOperationLog.rejectLog(form.getSubmitUserid(), form.getSubmitUsername(), form, task, id);
        optLog.setTaskDefinitionKey(task.getTaskDefinitionKey());
        optLog.setComment(comment);
        optLog.setTaskResult(WorkFlowUtil.decisionTranslate(getDecision(form)));
        flowOperationLogService.saveLog(optLog);
    }

    public void commonPassHandler(T form, Task task, String comment, String id) {
        taskService.claim(task.getId(), form.getSubmitUserid());
        //update status
        form.setBusinessState(STATE_DETAIL_ACTIVE);

        //如果是最后一个节点，complete以后会跳到endListener
        taskService.complete(task.getId());
        //如果是最后一个节点，此时不能保存。
        saveEntity(form);
        //pass log
        FlowOperationLog optLog = FlowOperationLog.passLog(form.getSubmitUserid(), form.getSubmitUsername(), form, task, id);
        optLog.setTaskDefinitionKey(task.getTaskDefinitionKey());
        optLog.setCheckItems(form.getCheckItemJson());
        optLog.setComment(comment);
        optLog.setTaskResult(WorkFlowUtil.decisionTranslate(getDecision(form)));
        flowOperationLogService.saveLog(optLog);
    }

    @Override
    public void delete(String entityId, String reason) {
        UserView user = TokenUtil.getUserFromAuthToken();
        ValidationResult validate = deleteValidate(entityId, user.getId());
        if (validate.isPass()) {
            T entity = load(entityId);
            //TODO: 删除正在进行的流程
            String procInstId = entity.getProcessInstanceId();
            final ProcessInstance runningProcInst = runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).active().singleResult();
            if (runningProcInst != null) {
                final HistoricTaskInstance runningTask = historyService.createHistoricTaskInstanceQuery().processInstanceId(procInstId).unfinished().singleResult();
                if (runningTask != null) {
                    historyService.deleteHistoricTaskInstance(runningTask.getId());
                }
                runtimeService.setVariable(entity.getProcessInstanceId(), VAR_KEY_REVOKE, DELETE_STATE_DELETE);
                runtimeService.deleteProcessInstance(entity.getProcessInstanceId(), DELETE_REASON_DELETE + "_" + user.getId());
            } else {
                //已结束的流程，不能使用deleteProcessInstance
                historyService.deleteHistoricProcessInstanceIfExists(procInstId);
            }
            entity.setBusinessState(Constants.STATE_DETAIL_DELETE);
            entity.setFinished(true);
            entity.setDelete(true);
            entity.setEndTime(LocalDateTime.now());
            saveEntity(entity);

            FlowOperationLog optLog = FlowOperationLog.deleteLog(user.getId(), user.getName(), entity, entityId, reason);
            flowOperationLogService.saveLog(optLog);
        } else {
            throw new BusinessException(validate.getDescription());
        }
    }

    public T beforeRevoke(String id, String operatorId) {
        //1. 已结束的
        final T entity = load(id);
        if (entity.isFinished()) {
            throw new BusinessException("已结束流程无法撤销!");
        }
        //2. 只有自己创建的可以撤销
        if (!operatorId.equals(entity.getCreateUserid())) {
            throw new BusinessException("只能撤销自己创建的流程!");
        }
        return entity;
    }

    public void doRevoke(T entity) {
        //listener会修改状态，所以先删除
        runtimeService.setVariable(entity.getProcessInstanceId(), VAR_KEY_REVOKE, DELETE_STATE_REVOKE);
        runtimeService.deleteProcessInstance(entity.getProcessInstanceId(), DEL_REASON_REVOKE);
        //3. 修改状态
        entity.setBusinessState(STATE_DETAIL_REVOKE);
        entity.setProcessState("INTERNALLY_TERMINATED");
        entity.setFinished(true);
        entity.setEndTime(LocalDateTime.now());
        saveEntity(entity);
    }

    @Transactional
    @Override
    public void revoke(String entityId, String operatorId, String operatorName) {
        WorkFlowUtil.ensureProperty(entityId, "审批编号（entityId)");
        //0. 校验
        T entity = beforeRevoke(entityId, operatorId);
        //1. 执行
        doRevoke(entity);
        //2. 添加log
        FlowOperationLog log = FlowOperationLog.revokeLog(operatorId, operatorName, entity, entityId);
        flowOperationLogService.saveLog(log);
    }

    public T setActiveTask(T entity) {
        Task task = taskService.createTaskQuery().active().processInstanceId(entity.getProcessInstanceId()).singleResult();
        if (task != null) {
            entity.setCurrentTaskAssigneeId(task.getAssignee());
            entity.setCurrentTaskId(task.getId());
            entity.setCurrentTaskName(task.getName());
            entity.setCurrentTaskDefId(task.getTaskDefinitionKey());
            entity.setCurrentTaskStartTime(TimeUtil.from(task.getCreateTime()));
            final User user = userService.getSimpleUserInfo(task.getAssignee());
            if (user != null) {
                entity.setCurrentTaskAssigneeName(user.getUsername());
            }
            String loginUserid = TokenUtil.getUseridFromAuthToken();
            entity.setApproveUser(loginUserid.equals(task.getAssignee()));
        }
        return entity;
    }

    public void buildActiveTask(T entity) {
        final ActRuTask task = entity.getCurrentTask();
        if (task != null) {
            entity.setCurrentTaskAssigneeId(task.getAssignee());
            entity.setCurrentTaskId(task.getId());
            entity.setCurrentTaskName(task.getName());
            entity.setCurrentTaskDefId(task.getTaskDefKey());
            entity.setCurrentTaskStartTime(task.getCreateTime());
            if (task.getTuser() != null) {
                entity.setCurrentTaskAssigneeName(task.getTuser().getName());
            }
        }
    }

    /**
     * 复制业务
     *
     * @param copyId 复制对象id
     */
    public T getCopyEntity(String copyId) {
        if (StringUtils.isBlank(copyId)) {
            throw new BusinessException("请选择一个流程提交");
        }

        //1. 获取copyId对应的实体
        T copyEntity = load(copyId);

        //清空其他数据
        clearEntityId(copyEntity);
        copyEntity.setProcessInstanceId(null);
        copyEntity.setBusinessState(null);
        copyEntity.setProcessState(null);
        copyEntity.setFinished(false);
        copyEntity.setEndTime(null);
        copyEntity.setDelete(false);
        copyEntity.setDeleteReason(null);
        clearBizProcessData(copyEntity);
        copyFile(copyEntity, copyEntity.getProcessDefinitionKey());
        return copyEntity;
    }

    @Override
    public void validateAttachment(T form) {
        try {
            final List<SystemFile> attachments = getFileAttachments(form);
            attachments.forEach(i -> {
                final String fullPath = i.getFullPath();
                final boolean exists = FileUtil.fileExists(fullPath);
                if (!exists) {
                    throw new BusinessException("附件上传失败(" + i.getOriginalName() + "), 请重新上传!");
                }
            });
        } catch (Exception e) {
            log.error("JSON转换失败！", e);
            throw new BusinessException("上传文件失败，请重新上传!");
        }
    }

    public List<SystemFile> getFileAttachments(T form) {
        if (StringUtils.isNotBlank(getAttachmentJson(form))) {
            return JsonUtil.toObject(getAttachmentJson(form), new TypeReference<List<SystemFile>>() {});
        }
        return new ArrayList<>();
    }

    /**
     * 预览前验证
     *
     * @param form 表单
     */
    abstract ValidationResult beforePreview(T form);


    abstract String getDecision(T form);

    /**
     * 审批通过后操作
     */
    abstract void passHandler(T form, Task task);

    abstract void rejectHandler(T form, Task task);

    abstract void afterApprove(T form);

    abstract void setApprovalResult(T form, T entity);

    abstract void setFileListJson(T entity, String json);

    /**
     * 获取附件json
     *
     * @param form 提交的表单
     */
    abstract String getAttachmentJson(T form);


    abstract void clearEntityId(T entity);

    void copyFile(T entity, String def) {
        final String service = getSaveServiceBy(def);
        try {
            final List<SystemFile> list = getFileAttachments(entity);
            List<SystemFile> newList = new ArrayList<>();
            list.forEach(i -> {
                File file = new File(i.getFullPath());
                final SystemFile newFile = fileService.copyFile(file, i.getOriginalName(), service, true, true);
                newList.add(newFile);
            });
            setFileListJson(entity, JsonUtil.toJson(newList));
        } catch (Exception e) {
            log.error("copy file error", e);
            setFileListJson(entity, null);
        }
    }

    /**
     * 导出
     * @param requestForm 按条件导出
     */
    public List<T> findExportData(R requestForm) {
        if (requestForm == null ) {
            //导出所有
            requestForm = createRequestForm();
            requestForm.setPage(1);
            requestForm.setLimit(99999);
        }
        if (requestForm.getLimit() == 0) {
            requestForm.setLimit(99999);
        }
        Page<T> page = commonFind(requestForm);
        final int total = (int)page.getTotalElements();
        if (total > 99999) {
            //TODO: 数据量过大时，应该多次批量导出
            requestForm.setLimit(total + 1);
            page = commonFind(requestForm);
        }
        return page.getContent();
    }

    private Page<T> commonFind(R requestForm) {
        switch (requestForm.getQueryMode()) {
            case QUERY_MODE_TODO -> {
                return findMyTodoByQueryPageable(requestForm);
            }
            case QUERY_MODE_DONE -> {
                return findMyDoneByQueryPageable(requestForm);
            }
            case QUERY_MODE_ALL -> {
                return findAllByQueryPageable(requestForm);
            }
            default -> {
                return findMyApplyByQueryPageable(requestForm);
            }
        }
    }

    /**
     * 含有多任务task的流程的审批记录导出
     * @param logs 审批记录
     * @param dto 报销详情导出dto
     */
    public void multiMgrProcessRecord(List<FlowOperationLog> logs, ReimburseExportDTO dto) {
        final List<FlowOperationLog> mgrList = logs.stream().filter(i -> dto.getMultiMgrDef().equals(i.getTaskDefinitionKey())).toList();
        if (mgrList.size() == 1) {
            //副总
            dto.setLeaderComment(mgrList.get(0).getComment());
            dto.setLeaderDate(TimeUtil.toYYYY_MM_DDString(mgrList.get(0).getTaskEndTime()));
            dto.setLeaderId(mgrList.get(0).getOperatorId());
        } else if (mgrList.size() > 1) {
            //主管
            dto.setManagerComment(mgrList.get(0).getComment());
            dto.setManagerDate(TimeUtil.toYYYY_MM_DDString(mgrList.get(0).getTaskEndTime()));
            dto.setManagerId(mgrList.get(0).getOperatorId());
            //副总
            dto.setLeaderComment(mgrList.get(1).getComment());
            dto.setLeaderDate(TimeUtil.toYYYY_MM_DDString(mgrList.get(1).getTaskStartTime()));
            dto.setLeaderId(mgrList.get(1).getOperatorId());
        }
        for (FlowOperationLog log : logs) {
            if (dto.getAccountDef().equals(log.getTaskDefinitionKey())) {
                //财务审批
                dto.setAcctComment(log.getComment());
                dto.setAcctDate(TimeUtil.toYYYY_MM_DDString(log.getTaskEndTime()));
                dto.setAcctId(log.getOperatorId());
            } else if (dto.getFinManagerDef().equals(log.getTaskDefinitionKey())) {
                dto.setFinManagerComment(log.getComment());
                dto.setFinManagerDate(TimeUtil.toYYYY_MM_DDString(log.getTaskEndTime()));
                dto.setFinManagerId(log.getOperatorId());
            } else if (dto.getCeoDef().equals(log.getTaskDefinitionKey())) {
                dto.setCeoComment(log.getComment());
                dto.setCeoDate(TimeUtil.toYYYY_MM_DDString(log.getTaskEndTime()));
                dto.setCeoId(log.getOperatorId());
            } else if (dto.getChiefDef().equals(log.getTaskDefinitionKey())) {
                dto.setChiefComment(log.getComment());
                dto.setChiefDate(TimeUtil.toYYYY_MM_DDString(log.getTaskEndTime()));
                dto.setChiefId(log.getOperatorId());
            } else if (dto.getCashierDef().equals(log.getTaskDefinitionKey())) {
                dto.setCashierComment(log.getComment());
                dto.setCashierDate(TimeUtil.toYYYY_MM_DDString(log.getTaskEndTime()));
                dto.setCashierId(log.getOperatorId());
            }
        }
    }

    @Override
    public void export(R requestForm, HttpServletResponse response, String templatePath, String newFileName, Class<T> dataClass) throws IOException {
        Assert.notNull(response, "response is null!");
        Assert.notNull(templatePath, "templatePath is null!");
        if (!Files.isRegularFile(Paths.get(templatePath))) {
            throw new BusinessException("未添加导出模板!");
        }
        final List<T> all = findExportData(requestForm);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + newFileName);
        EasyExcel.write(response.getOutputStream(), dataClass).withTemplate(templatePath).sheet().doFill(all);
    }

    /**
     * easyexcel中插入签名
     * @param userid 用户id
     * @return WriteCellData<Void>
     */
    WriteCellData<Void> setExcelSig(String userid) throws IOException {
        if (StringUtils.isNotBlank(userid)) {
            final UserSignature msig = signatureService.getSignatureByUserid(userid);
            if (msig != null) {
                File sf = new File(signatureService.getSignatureDir() + msig.getFileName());
                return createImageData(sf);
            }
        }
        return null;
    }

    WriteCellData<Void> setExcelSigWithMargin2(String userid) throws IOException {
        if (StringUtils.isNotBlank(userid)) {
            final UserSignature msig = signatureService.getSignatureByUserid(userid);
            if (msig != null) {
                File sf = new File(signatureService.getSignatureDir() + msig.getFileName());
                return createImageDataWithMargin2(sf);
            }
        }
        return null;
    }

    void createProcessRecordSig(ReimburseExportDTO dto) {
        try {
            dto.setManagerSig(setExcelSigWithMargin2(dto.getManagerId()));
            dto.setLeaderSig(setExcelSigWithMargin2(dto.getLeaderId()));
            dto.setAcctSig(setExcelSigWithMargin2(dto.getAcctId()));
            dto.setFinManagerSig(setExcelSigWithMargin2(dto.getFinManagerId()));
            dto.setCeoSig(setExcelSigWithMargin2(dto.getCeoId()));
            dto.setChiefSig(setExcelSigWithMargin2(dto.getChiefId()));
            dto.setCashierSig(setExcelSigWithMargin2(dto.getCashierId()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void skipEmptyUserTask(T form) {
        final Task currentTask = taskService.createTaskQuery().processInstanceId(form.getProcessInstanceId()).active().singleResult();
        if (currentTask == null) {
            return;
        }
        final String assignee = currentTask.getAssignee();
        if (StringUtils.isNotBlank(assignee)) {
            return;
        }
        FlowOperationLog optLog = FlowOperationLog.autoPassLog(form, currentTask, getEntityId(form));
        flowOperationLogService.saveLog(optLog);
        taskService.complete(currentTask.getId());
        skipEmptyUserTask(form);
    }

}
