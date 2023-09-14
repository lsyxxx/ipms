package com.abt.flow.service.impl;

import com.abt.common.util.MessageUtil;
import com.abt.flow.config.FlowableConstant;
import com.abt.flow.model.ProcessVo;
import com.abt.flow.model.entity.FlowBusinessBase;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.service.FlowBaseService;
import com.abt.flow.service.FlowOperationLogService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.dto.UserView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.GrantedAuthority;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * 流程操作默认实现
 *
 */
@Slf4j
public abstract class AbstractDefaultFlowService<T, E extends FlowBusinessBase> implements FlowBaseService {
    private RuntimeService runtimeService;
    private TaskService taskService;
    private HistoryService historyService;
    private RepositoryService repositoryService;
    private FlowableConstant flowableConstant;

    private MessageSourceAccessor messages = MessageUtil.getAccessor();
    private FlowOperationLogService flowOperationLogService;


    protected AbstractDefaultFlowService(RuntimeService runtimeService, TaskService taskService, HistoryService historyService, RepositoryService repositoryService, FlowableConstant flowableConstant, FlowOperationLogService flowOperationLogService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.historyService = historyService;
        this.repositoryService = repositoryService;
        this.flowableConstant = flowableConstant;
        this.flowOperationLogService = flowOperationLogService;
    }

    @Override
    public void completeTask(UserView user, String procId) {
        Task activeTask = getActiveTask(procId, messages.getMessage("flow.service.AbstractDefaultFlowService.completeTask.error"));
        String taskId = activeTask.getId();
        beforeCompleteTask(taskId, procId);
        taskService.complete(taskId);
    }

    /**
     * 完成任务前操作
     * 比如更新业务数据，设置流程变量
     */
    abstract void beforeCompleteTask(String taskId, String procInstId);

    ProcessInstance getActiveProcessInstance(String procId) {
        return runtimeService.createProcessInstanceQuery().active().processInstanceId(procId).singleResult();
    }

    @Override
    public void rejectTask(UserView user, String procId) {
        log.info("开始执行[拒绝任务], 流程实例id: {}", procId);
        runtimeService.deleteProcessInstance(procId, messages.getMessage("common.flow.deleteReason.reject", user.simpleInfo()));
    }

    @Override
    public List<FlowOperationLog> getOperationLogs(String processInstanceId) {
        return flowOperationLogService.getByProcessInstanceId(processInstanceId);;
    }

    @Override
    public InputStream getHighLightedTaskPngDiagram(String processInstanceId, String processDefinitionId) {
        return null;
    }

    @Override
    public void deleteProcess(String processInstanceId, String delReason, UserView user) {
        log.info("开始执行删除流程deleteProcess(), 流程实例id: {}, 删除原因: {}, 删除用户: {}", processInstanceId, delReason, user.simpleInfo());
        //验证权限TODO
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        if (StringUtils.isBlank(delReason)) {
            delReason = messages.getMessage("comm.flow.deleteReason.default");
        }
        getActiveTask(processInstanceId, delReason);
        runtimeService.deleteProcessInstance(processInstanceId, delReason);
    }

    @Override
    public void cancelProcess(String processInstanceId) {
        log.info("开始执行取消流程cancelProcess(), 流程实例id: {}", processInstanceId);


    }

    @Override
    public Task getActiveTask(String processInstanceId, String errMsg) {
        Task activeTask = taskService.createTaskQuery().active().processInstanceId(processInstanceId).singleResult();
        if (activeTask == null) {
            log.error("流程[{}]未查询到正在进行的任务", processInstanceId);
            if (errMsg == null) {
                errMsg = MessageUtil.format("common.flow.noActiveTask", processInstanceId);
            }
            throw new BusinessException(errMsg);
        }
        return activeTask;
    }

    @Override
    public ProcessVo<T> check(UserView user, ProcessVo<T> vo) {
        return null;
    }

    @Override
    public ProcessVo<T> start(UserView user, ProcessVo<T> vo) {
        return null;
    }
}
