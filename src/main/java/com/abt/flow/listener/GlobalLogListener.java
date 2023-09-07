package com.abt.flow.listener;

import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.repository.FlowOperationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 全局监听
 * 写入日志
 */
@Slf4j
@Component
public class GlobalLogListener extends FlowBaseListener implements FlowableEventListener {


    private final FlowOperationLogRepository flowOperationLogRepository;

    public GlobalLogListener(FlowOperationLogRepository flowOperationLogRepository) {
        this.flowOperationLogRepository = flowOperationLogRepository;
    }


    private FlowOperationLog createLog(TaskEntity event) {
        FlowOperationLog optLog = new FlowOperationLog();
        optLog.setProcInstId(event.getProcessInstanceId());
        optLog.setAction(event.getName());
        optLog.setExecutionId(event.getExecutionId());
        optLog.setOperateDate(LocalDateTime.now());
        optLog.setTaskId(event.getId());
        optLog.setProcDefId(event.getProcessDefinitionId());
        optLog.setActivityId(event.getId());
        optLog.setActivityName(event.getName());
        optLog.setOperateUser(event.getAssignee());
        return optLog;
    }

    @Override
    public void onEvent(FlowableEvent event) {
        log.info("-------- GlobalLogListener.onEvent() 执行 ---------------- ");
        TaskEntity taskEntity = (TaskEntity) ((FlowableEntityEventImpl) event).getEntity();

        if (taskNotComplete(event)) {
            return;
        }

        flowOperationLogRepository.save(createLog(taskEntity));

    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
