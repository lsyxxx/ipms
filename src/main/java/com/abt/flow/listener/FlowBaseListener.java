package com.abt.flow.listener;

import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.repository.FlowOperationLogRepository;
import com.abt.sys.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;

/**
 * 流程监听器
 */
@AllArgsConstructor
public class FlowBaseListener {


    private UserRepository userRepository;

    /**
     * 任务是否完成
     * task: claim -> create -> complete -> delete
     */
    boolean isTaskComplete(FlowableEvent event) {
        return FlowableEngineEventType.TASK_COMPLETED.equals(event.getType());
    }

    /**
     * 任务未完成
     */
    boolean taskNotComplete(FlowableEvent event) {
        return !isTaskComplete(event);
    }

    protected TaskEntity getTaskEntity(FlowableEvent event) {
        return (TaskEntity) ((FlowableEntityEventImpl) event).getEntity();
    }


    protected FlowOperationLog createLog(TaskEntity event) {
        FlowOperationLog optLog = new FlowOperationLog();
        optLog.setProcInstId(event.getProcessInstanceId());
        optLog.setAction(event.getName());
        optLog.setExecutionId(event.getExecutionId());
        optLog.setOperateDate(LocalDateTime.now());
        optLog.setTaskId(event.getId());
        optLog.setProcDefId(event.getProcessDefinitionId());
        optLog.setActivityId(event.getId());
        optLog.setActivityName(event.getName());
        optLog.setOperateUserid(event.getAssignee());
        optLog.setOperateUsername(userRepository.getSimpleUserInfo(event.getAssignee()).getUsername());
        return optLog;
    }
}
