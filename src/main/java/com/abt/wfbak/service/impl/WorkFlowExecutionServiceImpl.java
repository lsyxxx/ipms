package com.abt.wfbak.service.impl;

import com.abt.common.model.User;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.service.UserService;
import com.abt.wfbak.entity.Reimburse;
import com.abt.wfbak.exception.MissingRequiredParameterException;
import com.abt.wfbak.model.ApprovalTask;
import com.abt.wfbak.model.ReimburseForm;
import com.abt.wfbak.model.TaskDTO;
import com.abt.wfbak.service.ReimburseService;
import com.abt.wfbak.service.WorkFlowExecutionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.impl.juel.ExpressionFactoryImpl;
import org.camunda.bpm.impl.juel.SimpleContext;
import org.camunda.bpm.impl.juel.jakarta.el.ExpressionFactory;
import org.camunda.bpm.impl.juel.jakarta.el.ValueExpression;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.StartEventImpl;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final RepositoryService repositoryService;
    private final Map<String, BpmnModelInstance> bpmnModelInstanceMap;
    private final Map<String, ProcessDefinition> processDefinitionMap;
    private final UserService<User, User> userService;

    public static final String VARS_ENTITY_ID = "entityId";

    /**
     * 当前审批结果
     */
    public static final String VARS_CUR_RESULT = "currentResult";

    public WorkFlowExecutionServiceImpl(RuntimeService runtimeService, TaskService taskService, HistoryService historyService, ReimburseService reimburseService, IdentityService identityService, RepositoryService repositoryService, @Qualifier("bpmnModelInstanceMap") Map<String, BpmnModelInstance> bpmnModelInstanceMap, @Qualifier("processDefinitionMap") Map<String, ProcessDefinition> processDefinitionMap, @Qualifier("sqlServerUserService") UserService userService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
        this.reimburseService = reimburseService;
        this.identityService = identityService;
        this.repositoryService = repositoryService;
        this.bpmnModelInstanceMap = bpmnModelInstanceMap;
        this.processDefinitionMap = processDefinitionMap;
        this.userService = userService;
    }


    @Override
    public List<ApprovalTask> previewFlow(ReimburseForm form) {
        String procDefKey = form.getProcessDefinitionKey();
        ensureProcessDefinitionKey(form);
        Map<String, Object> vars = form.variableMap();
        BpmnModelInstance bpmnModelInstance = bpmnModelInstanceMap.get(form.getProcessDefinitionKey());
        final Collection<StartEvent> startEvents = bpmnModelInstance.getModelElementsByType(StartEvent.class);
        StartEventImpl startEvent = (StartEventImpl) startEvents.iterator().next();
        List<FlowNode> previewList = new ArrayList<>();
        findActivityNodes(startEvent, previewList, vars);
        //去掉startEvent和endEvent
        previewList = previewList.stream().filter(i -> !(i instanceof StartEvent || i instanceof EndEvent)).collect(Collectors.toList());

        //生成返回给前端的对象
        List<ApprovalTask> list = new ArrayList<>();
        for(FlowNode node : previewList) {
            ApprovalTask task = new ApprovalTask();
            TaskDTO dto = new TaskDTO();
            dto.setProcessDefinitionKey(procDefKey);
            dto.setProcessDefinitionId(form.getProcessDefinitionId());
            dto.setTaskDefKey(node.getId());
            dto.setTaskDefName(node.getName());
            if (node instanceof UserTask u) {
                final Collection<CamundaProperty> extensionProperties = WorkFlowQueryServiceImpl.queryUserTaskBpmnModelExtensionProperties(bpmnModelInstance, node.getId());
                task.setProperties(extensionProperties);
                if (task.isApplyNode()) {
                    dto.setAssigneeId(form.getStarterId());
                    dto.setAssigneeName(form.getStarterName());
                } else {
                    String assigneeId = u.getCamundaAssignee();
                    //指定用户才能解析
                    if (task.isSpecific()) {
                        final User simpleUserInfo = userService.getSimpleUserInfo(new User(assigneeId));
                        if (simpleUserInfo != null) {
                            dto.setAssigneeId(assigneeId);
                            dto.setAssigneeName(simpleUserInfo.getUsername());
                        } else {
                            log.warn("未查询到用户-" + assigneeId);
                        }
                    }

                }
            }
            task.addTask(dto);
            task.setTaskDefId(node.getId());
            task.setTaskDefName(node.getName());
            list.add(task);
        }

         return list;
    }


    /**
     * 递归查找node
     * @param startNode 起始node，一般为startEvent
     * @param list 查找的所有node集合
     * @param vars 流程参数
     */
    public void findActivityNodes(FlowNode startNode, List<FlowNode> list, Map<String, Object> vars) {
        list.add(startNode);
        final FlowNode nextNode = findUniqueNode(startNode, vars);
        if (nextNode != null) {
            findActivityNodes(nextNode, list, vars);
        }
    }

    /**
     * 根据流程参数寻找node连接的唯一node
     * @param node 起始node
     * @param vars 流程参数map
     * @return 连接node
     */
    public FlowNode findUniqueNode(FlowNode node, Map<String, Object> vars) {
        final Collection<SequenceFlow> outgoing = node.getOutgoing();
        if (outgoing.isEmpty()) {
            return null;
        } else if (outgoing.size() == 1) {
            return node.getSucceedingNodes().singleResult();
        } else {
            //多条连线
            ExpressionFactory factory = new ExpressionFactoryImpl();
            SimpleContext context = new SimpleContext();
            for (SequenceFlow sequenceFlow : outgoing) {
                final ConditionExpression conditionExpression = sequenceFlow.getConditionExpression();
                if (conditionExpression == null) {
                    //没有表达式
                    continue;
                }
                final String type = conditionExpression.getType();
                if (!"bpmn:tFormalExpression".equals(type)) {
                    throw new BusinessException("条件不是juel表达式，无法解析 - " + conditionExpression.getTextContent());
                }
                //--- 暂时仅解析el表达式
                //其他类型可能需要用到elResolver
                final String text = conditionExpression.getTextContent();
                for(Map.Entry<String, Object> entry : vars.entrySet()) {
                    String k = entry.getKey();
                    //判断表达式中是否包含参数。
                    //不包含的话，exp.getValue(context) 报错：找不到property
                    if (text.contains(k)) {
                        Object v = entry.getValue();
                        context.setVariable(k, factory.createValueExpression(v, v.getClass()));
                        ValueExpression exp = factory.createValueExpression(context, conditionExpression.getTextContent(), Boolean.class);
                        Object value = exp.getValue(context);
                        String result = value.toString();
                        if (Boolean.parseBoolean(result)) {
                            return sequenceFlow.getTarget();
                        }
                    }
                }
            }
        }

        return null;
    }


    public String userApplyBusinessKey(String userid, String username) {
        return "USER_APPLY_" + userid + "_" + username;
    }


    @Override
    @Transactional
    public Reimburse apply(ReimburseForm form) {
        ensureProcessDefinitionKey(form);
        form.setStarterId(form.getSubmitUserid());
        form.setStarterName(form.getSubmitUsername());
        setAuthUser(form.getStarterId());
        Map<String, Object> vars = form.variableMap();
        final ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(form.getProcessDefinitionKey()).latestVersion().active().singleResult();
        form.setProcessDefinitionId(processDefinition.getId());
        final ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), userApplyBusinessKey(form.getStarterId(), form.getStarterName()), vars);
        final Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().singleResult();
        task.setAssignee(form.getStarterId());
        taskService.saveTask(task);

        form.setProcessInstanceId(processInstance.getId());

        Reimburse reimburse = reimburseService.saveEntity(form);
        String id = reimburse.getId();
        runtimeService.setVariable(processInstance.getId(), VARS_ENTITY_ID, id);

        clearAuthUser();
        return reimburse;
    }

    public static final String DELETE_REASON_REJECT_BY = "RejectBy_";

    public String deleteReasonReject(String userid, String username) {
        return DELETE_REASON_REJECT_BY + userid + "_" + username;
    }

    public void setAuthUser(String userid) {
        identityService.setAuthenticatedUserId(userid);
    }

    public void clearAuthUser() {
        identityService.clearAuthentication();
    }


    @Override
    public void approve(ReimburseForm form) {
        ensureProcessId(form);
        setAuthUser(form.getSubmitUserid());
        String procId = form.getProcessInstanceId();
        Task task = taskService.createTaskQuery().processInstanceId(procId).active().singleResult();
        task.setAssignee(form.getSubmitUserid());
        if (StringUtils.isNotBlank(form.getComment())) {
            taskService.createComment(task.getId(), form.getProcessInstanceId(), form.getComment());
        }

        Reimburse reimburse = reimburseService.queryBy(form.getId()).get();
        if (form.isReject()) {
            taskService.saveTask(task);
            String deleteReason = deleteReasonReject(form.getSubmitUserid(), form.getSubmitUsername());
            runtimeService.deleteProcessInstance(form.getProcessInstanceId(), deleteReason);
            reimburse.taskReject(form.getSubmitUserid(), form.getSubmitUsername());
        } else {
            //pass
            taskService.saveTask(task);
            taskService.complete(task.getId());
            reimburse.taskPass();
        }
        reimburseService.saveEntity(reimburse);
        clearAuthUser();
    }

    public void deleteProcess(String processId, String userid, String username) {

    }

    public static void ensureProcessId(ReimburseForm form) {
        if (StringUtils.isNotBlank(form.getProcessInstanceId())) {
            return;
        }
        throw new MissingRequiredParameterException("ProcessInstanceId(流程实例id)");
    }

    public static void ensureProcessDefinitionId(ReimburseForm form) {
        if (StringUtils.isNotBlank(form.getProcessDefinitionId())) {
            return;
        }
        throw new MissingRequiredParameterException("ProcessDefinitionId(流程定义id)");
    }

    public static void ensureProcessDefinitionKey(ReimburseForm form) {
        if (StringUtils.isNotBlank(form.getProcessDefinitionKey())) {
            return;
        }
        throw new MissingRequiredParameterException("ProcessDefinitionKey(流程定义key)");
    }

}
