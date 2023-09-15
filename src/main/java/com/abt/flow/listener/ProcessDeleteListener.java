package com.abt.flow.listener;

import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.repository.FlowOperationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 流程删除时
 */
@Component
@Slf4j
public class ProcessDeleteListener extends FlowBaseListener implements FlowableEventListener {

    private final FlowOperationLogRepository flowOperationLogRepository;

    private final ApplicationContext context;

    public ProcessDeleteListener( FlowOperationLogRepository flowOperationLogRepository, ApplicationContext context) {
        super( flowOperationLogRepository, context);
        this.flowOperationLogRepository = flowOperationLogRepository;
        this.context = context;
    }


    @Override
    public void onEvent(FlowableEvent event) {
        if (!isProcessDelete(event)) {
            return;
        }
        log.info("--------- 开始执行[流程删除监听器ProcessDeleteListener] ----------------");
        TaskEntity taskEntity = getTaskEntity(event);

        FlowOperationLog flowLog = createLog(taskEntity);



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
