package com.abt.flow.listener;

import com.abt.flow.model.ProcessState;
import com.abt.flow.model.entity.BizFlowRelation;
import com.abt.flow.model.entity.FlowOperationLog;
import com.abt.flow.repository.BizFlowRelationRepository;
import com.abt.flow.repository.FlowOperationLogRepository;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 流程监听器
 */
public class FlowBaseListener {

    private BizFlowRelationRepository bizFlowRelationRepository;
    private FlowOperationLogRepository flowOperationLogRepository;
    private RuntimeService runtimeService;

    public FlowBaseListener(BizFlowRelationRepository bizFlowRelationRepository, FlowOperationLogRepository flowOperationLogRepository, RuntimeService runtimeService) {
        this.bizFlowRelationRepository = bizFlowRelationRepository;
        this.flowOperationLogRepository = flowOperationLogRepository;
        this.runtimeService = runtimeService;
    }

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


    /**
     * 流程是否被删除
     * @param event
     * @return
     */
    boolean isProcessDelete(FlowableEvent event) {
        return FlowableEngineEventType.PROCESS_CANCELLED.equals(event);
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
        optLog.setOperateUser(event.getAssignee());
        return optLog;
    }

    protected BizFlowRelation create(TaskEntity task) {
        BizFlowRelation obj = bizFlowRelationRepository.findByProcInstId(task.getProcessInstanceId());
        if (obj == null) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            obj = new BizFlowRelation();
            obj.setProcDefId(task.getProcessDefinitionId());
            obj.setProcInstId(task.getProcessInstanceId());
            obj.setCustomName(processInstance.getBusinessKey());
            obj.setStarterId(task.getAssignee());
            obj.setState(ProcessState.Active.code());
            obj.setStartDate(LocalDate.now());
        }

        update(task, obj);
        return obj;
    }



    /**
     * 已有的流程更新
     * @param task 当前完成的task
     */
    protected BizFlowRelation update(TaskEntity task, BizFlowRelation obj) {
        obj.setTaskId(task.getId());
        obj.setActivityName(task.getName());
        obj.setCurrentUser(task.getAssignee());
        obj.setLastUpdateDate(LocalDate.now());
        obj.setLastUpdateUser(task.getAssignee());
        return obj;
    }
}
