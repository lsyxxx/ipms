package com.abt.flow.listener;

import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.repository.FlowOperationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 所有task完成的操作
 */
@Component
@Slf4j
public class GlobalTaskCompleteListener extends FlowBaseListener implements FlowableEventListener {


    private final BizFlowRelationRepository bizFlowRelationRepository;
    private final FlowOperationLogRepository flowOperationLogRepository;

    private final ApplicationContext context;

    public GlobalTaskCompleteListener(BizFlowRelationRepository bizFlowRelationRepository, FlowOperationLogRepository flowOperationLogRepository, ApplicationContext context) {
        super(bizFlowRelationRepository, flowOperationLogRepository, context);
        this.bizFlowRelationRepository = bizFlowRelationRepository;
        this.flowOperationLogRepository = flowOperationLogRepository;
        this.context = context;
    }

    @Override
    public void onEvent(FlowableEvent event) {
        TaskEntity taskEntity = getTaskEntity(event);
        log.info("----- 开始执行[流程完成监听器 GlobalTaskCompleteListener] Task: id: {}, name: {}", taskEntity.getId(), taskEntity.getName());

        bizFlowRelationRepository.save(create(taskEntity));
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
