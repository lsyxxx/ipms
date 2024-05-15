package com.abt.wf.service;

import com.abt.common.model.User;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.TaskWrapper;

import java.util.List;

public interface ActivitiService {
    WorkflowBase findFinanceTask(String assignee);

    List<User> findDefaultCopyUsers();

    WorkflowBase findUserTodoLatest1ByProcessDefinitionKeys(String userid, List<String> keys);

    long countUserFinanceTodo(String userid);

    long countUserTodo(String userid, List<String> keys);
}
