package com.abt.wf.service.impl;

import com.abt.common.model.User;
import com.abt.wf.config.Constants;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.service.ActivitiService;
import com.abt.wf.service.BusinessService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Slf4j
public class ActivitiServiceImpl implements ActivitiService {

    private final WorkFlowConfig workFlowConfig;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final RuntimeService runtimeService;

    private final Map<String, BusinessService> serviceMap;

    public ActivitiServiceImpl(WorkFlowConfig workFlowConfig, TaskService taskService, HistoryService historyService, RuntimeService runtimeService, Map<String, BusinessService> serviceMap) {
        this.workFlowConfig = workFlowConfig;
        this.taskService = taskService;
        this.historyService = historyService;
        this.runtimeService = runtimeService;
        this.serviceMap = serviceMap;
    }

    @Override
    public WorkflowBase findFinanceTask(String assignee) {
        return findUserTodoLatest1ByProcessDefinitionKeys(assignee, WorkFlowConfig.financeWorkflowDef);
    }

    @Override
    public List<User> findDefaultCopyUsers() {
        return workFlowConfig.workflowDefaultCopy();
    }

    @Override
    public WorkflowBase findUserTodoLatest1ByProcessDefinitionKeys(String userid, List<String> keys) {
        String keyIn = keys.stream().map(item -> "'" + item + "'").collect(Collectors.joining(", "));
        final List<Task> list = taskService.createNativeTaskQuery()
                .sql("SELECT * FROM ACT_RU_TASK t " +
                        "left join ACT_RE_PROCDEF D on t.PROC_DEF_ID_ = D.ID_  " +
                        "left join ACT_RU_IDENTITYLINK i on t.ID_ = i.TASK_ID_ " +
                        "WHERE t.TASK_DEF_KEY_ NOT LIKE '%apply%' " +
                        "and (t.ASSIGNEE_ = #{userid} or (t.ASSIGNEE_ is null and i.USER_ID_ = #{userid})) " +
                        "and t.SUSPENSION_STATE_ = 1 " +
                        "and D.KEY_ in (" + keyIn + ") " +
                        "order by t.CREATE_TIME_ desc;"
                )
                .parameter("userid", userid)
                .list();
        //去掉申请节点
        if (!CollectionUtils.isEmpty(list)) {
            //1. 获取对应的业务实体
            Task task = list.get(0);
            System.out.println(task.getProcessInstanceId());
            String procId = task.getProcessInstanceId();
            final HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();
            final VariableInstance variableInstance = runtimeService.createVariableInstanceQuery().processInstanceIdIn(procId).variableName(Constants.VAR_KEY_ENTITY).singleResult();
            final String entityId = variableInstance.getValue().toString();
            BusinessService businessService = serviceMap.get(historicProcessInstance.getProcessDefinitionKey());
            final WorkflowBase load = businessService.load(entityId);
            return load;
        }
        return null;
    }

    @Override
    public long countUserFinanceTodo(String userid) {
        return countUserTodo(userid, WorkFlowConfig.financeWorkflowDef);
    }

    @Override
    public long countUserTodo(String userid, List<String> keys) {
        String keyIn = keys.stream().map(item -> "'" + item + "'").collect(Collectors.joining(", "));
        final long count = taskService.createNativeTaskQuery()
                .sql("SELECT count(1) FROM ACT_RU_TASK t " +
                        "left join ACT_RE_PROCDEF D on t.PROC_DEF_ID_ = D.ID_  " +
                        "left join ACT_RU_IDENTITYLINK i on t.ID_ = i.TASK_ID_ " +
                        "WHERE t.TASK_DEF_KEY_ NOT LIKE '%apply%' " +
                        "and (t.ASSIGNEE_ = #{userid} or (t.ASSIGNEE_ is null and i.USER_ID_ = #{userid})) " +
                        "and t.SUSPENSION_STATE_ = 1 " +
                        "and D.KEY_ in (" + keyIn + "); "
                )
                .parameter("userid", userid)
                .count();
        return count;
    }
}
