package com.abt.wf.service;

import com.abt.common.model.User;
import com.abt.wf.model.TaskWrapper;

import java.util.List;

public interface ActivitiService {
    List<TaskWrapper> findFinanceTask(String assignee, String... defKeys);

    List<User> findDefaultCopyUsers();
}
