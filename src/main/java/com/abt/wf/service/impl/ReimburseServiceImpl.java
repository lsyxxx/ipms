package com.abt.wf.service.impl;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.model.User;
import com.abt.common.util.TokenUtil;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.repository.EmployeeRepository;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.model.*;
import com.abt.wf.repository.ReimburseRepository;
import com.abt.wf.repository.ReimburseTaskRepository;
import com.abt.wf.service.FlowOperationLogService;
import com.abt.wf.service.ReimburseService;
import com.abt.wf.util.WorkFlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.StartEventImpl;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.camunda.bpm.model.bpmn.instance.FlowNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Slf4j
public class ReimburseServiceImpl implements ReimburseService {

    private final IdentityService identityService;
    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;

    private final ReimburseRepository reimburseRepository;
    private final HistoryService historyService;

    private final FlowOperationLogService flowOperationLogService;

    private final Map<String, BpmnModelInstance> bpmnModelInstanceMap;

    private final UserService<User, User> userService;

    private final ReimburseTaskRepository reimburseTaskRepository;

    public static final String SERVICE_NAME = "费用报销";

    public ReimburseServiceImpl(IdentityService identityService, TaskService taskService, RuntimeService runtimeService,
                                RepositoryService repositoryService, ReimburseRepository reimburseRepository,
                                HistoryService historyService, FlowOperationLogService flowOperationLogService,
                                @Qualifier("bpmnModelInstanceMap") Map<String, BpmnModelInstance> bpmnModelInstanceMap,
                                @Qualifier("sqlServerUserService") UserService userService, EmployeeRepository employeeRepository, ReimburseTaskRepository reimburseTaskRepository) {
        this.identityService = identityService;
        this.taskService = taskService;
        this.runtimeService = runtimeService;
        this.repositoryService = repositoryService;
        this.reimburseRepository = reimburseRepository;
        this.historyService = historyService;
        this.flowOperationLogService = flowOperationLogService;
        this.bpmnModelInstanceMap = bpmnModelInstanceMap;
        this.userService = userService;
        this.reimburseTaskRepository = reimburseTaskRepository;
    }

    @Override
    public Map<String, Object> createVariableMap(ReimburseForm form) {
        form.variableMap();
        return form.getVariableMap();
    }


    /**
     * 流程名称，比如报销，开票等
     * @param form 业务数据
     */
    @Override
    public String businessKey(ReimburseForm form) {
        return Constants.SERVICE_RBS;
    }

    @Override
    public Map<String, Object> getVariableMap() {
        return null;
    }

    @Override
    public List<FlowOperationLog> getCompletedOperationLogByEntityId(String entityId) {
        //已完成的
        return flowOperationLogService.findLogsByEntityId(entityId);
    }

    public List<UserTaskDTO> wrapper(List<FlowOperationLog> operationLogs) {
        List<UserTaskDTO> list = new ArrayList<>();
        String procDefKey = "";
        if (!operationLogs.isEmpty()) {
            procDefKey = operationLogs.get(0).getProcessDefinitionKey();
        }
        BpmnModelInstance bpmnModelInstance = bpmnModelInstanceMap.get(procDefKey);
        for (FlowOperationLog opt : operationLogs) {
            UserTaskDTO dto = (UserTaskDTO) opt;
            final Collection<CamundaProperty> extensionProperties = WorkFlowUtil.queryUserTaskBpmnModelExtensionProperties(bpmnModelInstance, opt.getTaskDefinitionKey());
            dto.setProperties(extensionProperties);
            list.add(dto);
        }
        return list;
    }

    public UserTaskDTO createUserTaskWrapper(String procDefKey, String taskDefKey) {
        BpmnModelInstance bpmnModelInstance = bpmnModelInstanceMap.get(procDefKey);
        final Collection<CamundaProperty> extensionProperties = WorkFlowUtil.queryUserTaskBpmnModelExtensionProperties(bpmnModelInstance, taskDefKey);
        UserTaskDTO wrapper = new UserTaskDTO();
        wrapper.setProperties(extensionProperties);
        return wrapper;
    }

    @Override
    public List<UserTaskDTO> processRecord(ReimburseForm form) {
        List<FlowOperationLog> completed = getCompletedOperationLogByEntityId(form.getId());
        List<UserTaskDTO> record = wrapper(completed);
        if (record.isEmpty()) {
            return record;
        }
        //最后一个
        UserTaskDTO lastRecord = record.get(record.size() - 1);
        BpmnModelInstance bpmnModelInstance = bpmnModelInstanceMap.get(form.getProcessDefinitionKey());
        //TODO: id = taskKey?
        FlowNode startNode = bpmnModelInstance.getModelElementById(lastRecord.getTaskDefinitionKey());
        List<FlowNode> list = new ArrayList<>();
        WorkFlowUtil.findActivityNodes(startNode, list, form.variableMap());
        for (FlowNode node: list) {
            UserTaskDTO parent = flowNodeWrapper(node, form, bpmnModelInstance);
            record.add(parent);
        }

        return record;
    }

    @Override
    public void afterTask(ReimburseForm form) {

    }

    @Override
    public void apply(ReimburseForm form) {
        WorkFlowUtil.ensureProcessDefinitionKey(form);
        form.setCreateUserid(form.getSubmitUserid());
        form.setCreateUsername(form.getSubmitUsername());
        Map<String, Object> variableMap = this.createVariableMap(form);
        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(form.getProcessDefinitionKey()).latestVersion().active().singleResult();
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(form.getProcessDefinitionKey(), businessKey(form), variableMap);
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();
        task.setAssignee(form.getSubmitUserid());
        taskService.saveTask(task);

        form.setProcessDefinitionId(processDefinition.getId());
        form.setProcessInstanceId(processInstance.getId());
        form.setProcessState(HistoricProcessInstance.STATE_ACTIVE);
        form.setBusinessState(Constants.STATE_DETAIL_ACTIVE);
        Reimburse rbs = reimburseRepository.save(form);
        runtimeService.setVariable(processInstance.getId(), Constants.VAR_KEY_ENTITY, rbs.getId());
        clearAuthUser();

        FlowOperationLog optLog = FlowOperationLog.applyLog(form.getSubmitUserid(), form.getSubmitUsername(), form, task);
        flowOperationLogService.saveLog(optLog);
    }

    /**
     * 查询正在进行的task，使用historyService
     *
     * @param processInstanceId 流程实例id
     */
    private HistoricTaskInstance getActiveTask(String processInstanceId) {
        return historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).unfinished().singleResult();
    }

    @Override
    public void approve(ReimburseForm form) {
        WorkFlowUtil.ensureProcessId(form);
        setAuthUser(form.getSubmitUserid());
        String procId = form.getProcessInstanceId();
        Task task = taskService.createTaskQuery().processInstanceId(procId).active().singleResult();
        task.setAssignee(form.getSubmitUserid());
        FlowOperationLog optLog;
        Reimburse reimburse = findById(form.getId());
        if (form.isPass()) {
            taskService.saveTask(task);
            reimburse.setBusinessState(Constants.STATE_DETAIL_ACTIVE);
            optLog = FlowOperationLog.passLog(form.getSubmitUserid(), form.getSubmitUsername(), form, task);
            optLog.setComment(form.getComment());
            optLog.setTaskResult(form.getDecision());
        } else if (form.isReject()) {
            taskService.saveTask(task);
            runtimeService.deleteProcessInstance(task.getProcessInstanceId(), Constants.DELETE_REASON_REJECT + "_" + form.getSubmitUserid() + "_" + form.getSubmitUsername());
            reimburse.setBusinessState(Constants.STATE_DETAIL_REJECT);
            optLog = FlowOperationLog.rejectLog(form.getSubmitUserid(), form.getSubmitUsername(), form, task);
            optLog.setComment(form.getComment());
            optLog.setTaskResult(form.getDecision());
        } else {
            log.error("审批结果只能是pass/reject, 实际审批结果: {}", form.getDecision());
            throw new BusinessException("审批结果只能是pass/reject, 实际审批参数:" + form.getDecision());
        }

        reimburseRepository.save(reimburse);
        flowOperationLogService.saveLog(optLog);
        clearAuthUser();
    }

    @Override
    public Reimburse findById(String entityId) {
        final Optional<Reimburse> optionalReimburse = reimburseRepository.findById(entityId);
        if (optionalReimburse.isEmpty()) {
            throw new BusinessException("未查询到业务流程(Id: " + entityId + ")");
        }
        return optionalReimburse.get();
    }

    @Override
    public void saveEntity(Reimburse rbs) {
        reimburseRepository.save(rbs);
    }

    @Override
    public void setAuthUser(String userid) {
        identityService.setAuthenticatedUserId(userid);
    }

    @Override
    public void clearAuthUser() {
        identityService.clearAuthentication();
    }

    @Override
    public void revoke(String entityId) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        final ValidationResult validationResult = this.revokeValidate(entityId);
        if (validationResult.isPass()) {
            Reimburse rbs = this.load(entityId);
            runtimeService.deleteProcessInstance(rbs.getProcessInstanceId(), Constants.DELETE_REASON_REVOKE + "_" + user.getId() + "_" + user.getName());
            rbs.setBusinessState(Constants.STATE_DETAIL_REVOKE);
            reimburseRepository.save(rbs);

            FlowOperationLog optLog = FlowOperationLog.create(user.getId(), user.getName(), rbs.getProcessInstanceId(), rbs.getProcessDefinitionId(), rbs.getProcessDefinitionKey(),
                    SERVICE_NAME);
            optLog.setAction(ActionEnum.REVOKE.name());
            flowOperationLogService.saveLog(optLog);
        } else {
            throw new BusinessException(validationResult.getDescription());
        }
    }

    @Override
    public void delete(String entityId) {
        final UserView user = TokenUtil.getUserFromAuthToken();
        ValidationResult validationResult = deleteValidate(entityId);
        if (validationResult.isPass()) {
            Reimburse rbs = load(entityId);
            runtimeService.deleteProcessInstance(rbs.getProcessInstanceId(), Constants.DELETE_REASON_DELETE + "_" + user.getId() + "_" + user.getName());
            rbs.setBusinessState(Constants.STATE_DETAIL_DELETE);
            reimburseRepository.save(rbs);

            FlowOperationLog optLog = FlowOperationLog.create(user.getId(), user.getName(), rbs.getProcessInstanceId(), rbs.getProcessDefinitionId(), rbs.getProcessDefinitionKey(),
                    SERVICE_NAME);
            optLog.setAction(ActionEnum.DELETE.name());
            flowOperationLogService.saveLog(optLog);

        } else {
            throw new BusinessException(validationResult.getDescription());
        }
    }

    /**
     * 将flowNode包装为UserTaskDTO
     */
    public UserTaskDTO flowNodeWrapper(FlowNode node, ReimburseForm form, BpmnModelInstance bpmnModelInstance) {
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
                        log.warn("未查询到用户-" + assigneeId);
                    }
                }
            }
        }
        parent.addUserTaskDTO(child);
        return parent;
    }


    //预览和查看完成记录返回对象一致
    @Override
    public List<UserTaskDTO> preview(ReimburseForm form) {
        //验证必要参数
        WorkFlowUtil.ensureProcessDefinitionKey(form);
        if (form.getCost() == null) {
            throw new MissingRequiredParameterException("报销金额(cost)");
        }
        Map<String, Object> vars = form.variableMap();
        BpmnModelInstance bpmnModelInstance = bpmnModelInstanceMap.get(form.getProcessDefinitionKey());
        final Collection<StartEvent> startEvents = bpmnModelInstance.getModelElementsByType(StartEvent.class);
        List<FlowNode> list = WorkFlowUtil.getPreviewFlowNode(bpmnModelInstance, vars);
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
        return returnList;
    }



    /**
     * 删除校验
     * @param entityId 业务实体id
     */
    public ValidationResult deleteValidate(String entityId) {
        return ValidationResult.pass();
    }


    @Override
    public Reimburse load(String entityId) {
        final Optional<Reimburse> optionalReimburse = reimburseRepository.findById(entityId);
        if (optionalReimburse.isEmpty()) {
            log.info("未查询到业务实体! 实体id: {}", entityId);
            throw new BusinessException("未查询到流程业务(id: " + entityId + ")");
        }

        return optionalReimburse.get();
    }

    /**
     * 校验撤销
     * @param entityId 业务实体id
     */
    public ValidationResult revokeValidate(String entityId) {
        //1. 权限校验 -- 用户是否允许撤销/节点是否允许撤销
        // ValidationChain

        //2. 撤销条件：多少天内允许撤销


        return ValidationResult.pass();
    }


    @Override
    public List<ReimburseForm> findAllByCriteria(ReimburseRequestForm requestForm) {
        //query: 分页, 审批编号, 状态，创建人，创建时间
        return reimburseTaskRepository.findReimburseWithCurrenTaskPageable(requestForm.getPage(), requestForm.getLimit(),
                requestForm.getId(), requestForm.getState(), requestForm.getUserid(),
                requestForm.getStartDate(), requestForm.getEndDate());
    }

    @Override
    public List<ReimburseForm> findMyApplyByCriteria(ReimburseRequestForm requestForm) {
        return reimburseTaskRepository.findReimburseWithCurrenTaskPageable(requestForm.getPage(), requestForm.getLimit(),
                requestForm.getId(), requestForm.getState(), requestForm.getUserid(),
                requestForm.getStartDate(), requestForm.getEndDate());

    }
    @Override
    public List<ReimburseForm> findMyDoneByCriteria(ReimburseRequestForm requestForm) {
//        criteria: 分页, 审批编号, 状态，流程创建时间，参与人id, 待办/已办
        return reimburseTaskRepository.findTaskPageable(requestForm.getPage(), requestForm.getLimit(),
                requestForm.getId(), requestForm.getState(), requestForm.getUserid(),
                requestForm.getStartDate(), requestForm.getEndDate(), ReimburseTaskRepository.DONE);
    }

    @Override
    public List<ReimburseForm> findMyTodoByCriteria(ReimburseRequestForm requestForm) {
        return reimburseTaskRepository.findTaskPageable(requestForm.getPage(), requestForm.getLimit(),
                requestForm.getId(), requestForm.getState(), requestForm.getUserid(),
                requestForm.getStartDate(), requestForm.getEndDate(), ReimburseTaskRepository.TODO);
    }





}
