package com.abt.wf.model;

import com.abt.wf.entity.Reimburse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 查询报销list vo
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class ReimburseDTO extends TaskDTO{

    /**
     * 业务实例id
     */
    private String id;
    private LocalDateTime rbsDate;
    private String reason;
    private double cost;
    private int stateCode;
    private String stateDesc;
    /**
     * 报销类型
     */
    private String rbsType;

    public static final String STATE_APPROVING = "审批中";
    public static final String STATE_REJECT = "已拒绝";
    public static final String STATE_PASS = "已通过";

    public ReimburseDTO() {
        super();
    }


    public String translateState(int entityState) {
        switch (entityState) {
            case Reimburse.STATE_APPROVING -> this.setStateDesc(STATE_APPROVING);
            case Reimburse.STATE_PASS -> this.setStateDesc(STATE_PASS);
            case Reimburse.STATE_REJECT -> this.setStateDesc(STATE_REJECT);
            default -> log.warn("不存在的流程状态: " + entityState);
        }
        return this.getStateDesc();
    }

    public ReimburseDTO setTaskDTO(TaskDTO task) {
        this.setState(task.getState());
        this.setAssigneeId(task.getAssigneeId());
        this.setProcessDefinitionId(task.getProcessDefinitionId());
        this.setProcessDefinitionKey(task.getProcessDefinitionKey());
        this.setProcessInstanceId(task.getProcessInstanceId());
        this.setTaskDefKey(task.getTaskDefKey());
        this.setTaskDefName(task.getTaskDefName());
        this.setTaskDeleteReason(task.getTaskDeleteReason());
        this.setTaskDescription(task.getTaskDescription());
        this.setTaskEndTime(task.getTaskEndTime());
        this.setTaskInstanceId(task.getTaskInstanceId());
        this.setTaskStartTime(task.getTaskStartTime());
        this.setAssigneeName(task.getAssigneeName());
        this.setBusinessKey(task.getBusinessKey());
        this.setComments(task.getComments());
        this.setProcessDeleteReason(task.getProcessDeleteReason());
        this.setProcessEndTime(task.getProcessEndTime());
        this.setProcessStartTime(task.getProcessStartTime());
        this.setStartUserid(task.getStartUserid());
        this.setStartUsername(task.getStartUsername());
        return this;
    }

    public static ReimburseDTO from(Reimburse entity) {
        ReimburseDTO vo = new ReimburseDTO();
        vo.setId(entity.getId());
        vo.setProcessInstanceId(entity.getProcessInstanceId());
        vo.setProcessDefinitionId(entity.getProcessDefinitionId());
        vo.setRbsDate(entity.getRbsDate());
        vo.setReason(entity.getReason());
        vo.setCost(entity.getCost());
        vo.setStateCode(entity.getState());
        vo.setStartUserid(entity.getStarterId());
        vo.setStartUsername(entity.getStarterName());
        return vo;
    }

    public static ReimburseDTO from(TaskDTO task) {
        ReimburseDTO dto = new ReimburseDTO();
        dto.setState(task.getState());
        dto.setAssigneeId(task.getAssigneeId());
        dto.setProcessDefinitionId(task.getProcessDefinitionId());
        dto.setProcessDefinitionKey(task.getProcessDefinitionKey());
        dto.setProcessInstanceId(task.getProcessInstanceId());
        dto.setTaskDefKey(task.getTaskDefKey());
        dto.setTaskDefName(task.getTaskDefName());
        dto.setTaskDeleteReason(task.getTaskDeleteReason());
        dto.setTaskDescription(task.getTaskDescription());
        dto.setTaskEndTime(task.getTaskEndTime());
        dto.setTaskInstanceId(task.getTaskInstanceId());
        dto.setTaskStartTime(task.getTaskStartTime());
        dto.setAssigneeName(task.getAssigneeName());
        dto.setBusinessKey(task.getBusinessKey());
        dto.setComments(task.getComments());
        dto.setProcessDeleteReason(task.getProcessDeleteReason());
        dto.setProcessEndTime(task.getProcessEndTime());
        dto.setProcessStartTime(task.getProcessStartTime());
        dto.setStartUserid(task.getStartUserid());
        dto.setStartUsername(task.getStartUsername());
        return dto;
    }


}
