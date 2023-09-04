package com.abt.flow.listener;

import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.service.FlowOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.engine.delegate.event.impl.FlowableProcessEventImpl;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 全局监听
 * 写入日志
 */
@Slf4j
@Component
public class GlobalLogListener implements FlowableEventListener {


    private final FlowOperationLogService flowOperationLogService;

    public GlobalLogListener(FlowOperationLogService flowOperationLogService) {
        this.flowOperationLogService = flowOperationLogService;
    }

//    @Override
//    public void notify(DelegateTask delegateTask) {
//        log.info("--------- GlobalLogListener.notify() 执行 -----------------");
//        String eventName = delegateTask.getEventName();
//        if (TaskListener.EVENTNAME_COMPLETE.equals(eventName)) {
//            //任务完成事件
//            log.info("----------- 任务完成. eventName: {}, taskInfo: {}", eventName, delegateTask.toString());
//            flowOperationLogService.insertOne(createLog(delegateTask));
//        }
//    }


    private FlowOperationLog createLog(DelegateTask delegateTask) {
        FlowOperationLog optLog = new FlowOperationLog();
        optLog.setProcInstId(delegateTask.getProcessInstanceId());
        optLog.setAction(delegateTask.getEventName());
        optLog.setExecutionId(delegateTask.getExecutionId());
        optLog.setOperateDate(LocalDateTime.now());
        optLog.setTaskId(delegateTask.getId());
        optLog.setProcDefId(delegateTask.getProcessDefinitionId());

        return optLog;
    }

    @Override
    public void onEvent(FlowableEvent event) {
        log.info("-------- GlobalLogListener.onEvent() 执行 ----------------");
        FlowableProcessEventImpl eventImpl = (FlowableProcessEventImpl) event;
        log.info("-------- event info: {} ------------", eventImpl.toString());

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
