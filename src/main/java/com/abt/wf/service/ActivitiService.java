package com.abt.wf.service;

import com.abt.common.model.Page;
import com.abt.common.model.User;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.ActivitiRequestForm;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

import java.util.List;

public interface ActivitiService {
    /**
     *
     */
    List<WorkflowBase> findUserTodoAll(String userid, String query, int page, int limit);

    List<WorkflowBase>  findDoneByQuery(String userid, String query, int page, int limit);

    WorkflowBase findFinanceTask(String assignee);

    List<User> findDefaultCopyUsers();

    WorkflowBase findUserTodoLatest1ByProcessDefinitionKeys(String userid, List<String> keys);

    long countUserFinanceTodo(String userid);

    long countUserTodo(String userid, List<String> keys);

    void deleteProcessInstance(String processInstanceId, String deleteReason);

    /**
     * 待处理任务
     */
    Page<Task> runningTasks(ActivitiRequestForm form);

    /**
     * 所有流程
     */
    Page<HistoricProcessInstance> finishedProcess(ActivitiRequestForm form);

    Page<ProcessInstance> runtimeProcess(ActivitiRequestForm form);
}
