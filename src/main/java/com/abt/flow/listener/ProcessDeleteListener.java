package com.abt.flow.listener;

import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.repository.FlowOperationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.RuntimeService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

/**
 * 流程删除时
 */
@Component
@Slf4j
public class ProcessDeleteListener extends FlowBaseListener implements FlowableEventListener {

    private final BizFlowRelationRepository bizFlowRelationRepository;
    private final FlowOperationLogRepository flowOperationLogRepository;
    private final RuntimeService runtimeService;

    public ProcessDeleteListener(BizFlowRelationRepository bizFlowRelationRepository, FlowOperationLogRepository flowOperationLogRepository, RuntimeService runtimeService) {
        super(bizFlowRelationRepository, flowOperationLogRepository, runtimeService);
        this.bizFlowRelationRepository = bizFlowRelationRepository;
        this.flowOperationLogRepository = flowOperationLogRepository;
        this.runtimeService = runtimeService;
    }


    @Override
    public void onEvent(FlowableEvent event) {
        if (!isProcessDelete(event)) {
            return;
        }
        log.info("--------- 开始执行[流程删除监听器ProcessDeleteListener] ----------------");
        TaskEntity taskEntity = getTaskEntity(event);

        bizFlowRelationRepository.save(create(taskEntity));
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
