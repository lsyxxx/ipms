package com.abt.flow.listener;

import com.abt.common.model.Action;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.repository.FlowOperationLogRepository;
import com.abt.sys.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

/**
 * 流程删除时
 */
@Component
@Slf4j
public class ProcessDeleteListener extends FlowBaseListener implements FlowableEventListener {

    private final FlowOperationLogRepository flowOperationLogRepository;
    private final UserRepository userRepository;

    public ProcessDeleteListener(FlowOperationLogRepository flowOperationLogRepository, UserRepository userRepository) {
        super(userRepository);
        this.flowOperationLogRepository = flowOperationLogRepository;
        this.userRepository = userRepository;
    }


    @Override
    public void onEvent(FlowableEvent event) {
        log.info("--------- 开始执行[流程删除监听器ProcessDeleteListener] ----------------");
        TaskEntity taskEntity = getTaskEntity(event);
        FlowOperationLog flowLog = createLog(taskEntity);
        flowLog.setAction(Action.delete.description());

        flowOperationLogRepository.save(flowLog);

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
