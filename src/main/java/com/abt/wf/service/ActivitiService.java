package com.abt.wf.service;

import com.abt.common.model.User;
import com.abt.wf.entity.WorkflowBase;

import java.util.List;

public interface ActivitiService {
    /**
     *
     */
    List<WorkflowBase> findUserTodoAll(String userid, String query, int page, int limit);

    WorkflowBase findFinanceTask(String assignee);

    List<User> findDefaultCopyUsers();

    WorkflowBase findUserTodoLatest1ByProcessDefinitionKeys(String userid, List<String> keys);

    long countUserFinanceTodo(String userid);

    long countUserTodo(String userid, List<String> keys);

    void deleteProcessInstance(String processInstanceId, String deleteReason);
}
