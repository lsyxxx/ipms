package com.abt.wf.service.impl;

import com.abt.common.model.User;
import com.abt.wf.config.WorkFlowConfig;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.TaskWrapper;
import com.abt.wf.service.ActivitiService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 *
 */
@Service
@Slf4j
public class ActivitiServiceImpl implements ActivitiService {

    private final WorkFlowConfig workFlowConfig;
    private final TaskService taskService;

    public ActivitiServiceImpl(WorkFlowConfig workFlowConfig, TaskService taskService) {
        this.workFlowConfig = workFlowConfig;
        this.taskService = taskService;
    }

    @Override
    public List<TaskWrapper> findFinanceTask(String assignee, String... defKeys) {
        return List.of();
    }

    @Override
    public List<User> findDefaultCopyUsers() {
        return workFlowConfig.workflowDefaultCopy();
    }

    public WorkflowBase findUserTodoLatest1ByProcessDefinitionKeys(String userid, String ...keys) {
        final List<Task> list = taskService.createTaskQuery().active().taskAssignee(userid).processDefinitionKeyIn(keys)
                //仅显示前n个
                .orderByTaskCreateTime().desc().listPage(0, 1);
        if (!CollectionUtils.isEmpty(list)) {
            //1. 获取对应的业务实体
        }
        return new WorkflowBase();
    }

    public long countUserTodoByProcessDefinitionKeys(String ...keys) {
        return taskService.createTaskQuery().active().processDefinitionKeyIn(keys).count();
    }
}
