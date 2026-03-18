package com.abt.wf.service;

import com.abt.common.model.Page;
import com.abt.common.model.User;
import com.abt.wf.entity.WorkflowBase;
import com.abt.wf.model.ActivitiRequestForm;
import com.abt.wf.model.UserTodo;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

import java.util.List;

public interface ActivitiService {
    /**
     *
     */
    List<WorkflowBase> findUserTodoAll(String userid, String query, int page, int limit);


    List<User> findDefaultCopyUsers();


    //查询所有的待办流程
    UserTodo countTodoAll(String activeKey, String userid);

    List<Object> findTodoByDefKey(String defKey, String taskName, String query, String userid);

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
