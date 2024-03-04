package com.abt.wf.model;

import com.abt.wf.entity.Reimburse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
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
    private LocalDate rbsDate;
    private String reason;
    private double cost;
    private int stateCode;
    /**
     * 审批状态，记录列表中
     * 审批中/已结束/终止
     */
    private String stateDesc;
    /**
     * 审批结果，记录列表中
     * 审批未通过/审批已通过/--(没有审批结果)
     */
    private String resultDesc;
    /**
     * 审批详情中的状态
     * 已通过/已拒绝/已撤销/审批中
     */
    private String detailDesc;

    /**
     * 报销类型
     */
    private String rbsType;

    /*-----------------------
     * 审批状态。参考钉钉
     * ----------------------
     */
    public static final String STATE_APPROVING = "审批中";
    public static final String STATE_FINISHED = "已结束";
    public static final String STATE_END = "终止";

    /*-----------------------
     * 审批详情中的状态。参考钉钉
     * ----------------------
     */
    public static final String STATE_REJECT = "已拒绝";
    public static final String STATE_PASS = "已通过";
    public static final String STATE_CANCEL = "已撤销";

    /*---------------------
     * 审批结果。参考钉钉
     * --------------------
     */
    //没有产生结果，包含审批中/撤销
    public static final String STATE_APPROVAL_NONE = "--";
    public static final String STATE_APPROVAL_REJECT = "审批未通过";
    public static final String STATE_APPROVAL_PASS = "审批通过";

    /**
     * 暂存数据不在记录中查询得到。仅状态标识
     */
    public static final String STATE_TEMP = "暂存";




    /**
     * 使用create()创建对象
     */
    public ReimburseDTO() {
        super();
    }


    /**
     * 审批状态描述
     * @param entityState
     * @return
     */
    public String stateDesc(int entityState) {
        switch (entityState) {
            case Reimburse.STATE_APPROVING -> this.setStateDesc(STATE_APPROVING);
            case Reimburse.STATE_PASS -> this.setStateDesc(STATE_PASS);
            case Reimburse.STATE_REJECT -> this.setStateDesc(STATE_REJECT);
            case Reimburse.STATE_TEMP -> this.setStateDesc(STATE_TEMP);
//            case Reimburse.STATE_END -> this.setStateDesc(STATE_END);
            default -> log.warn("不存在的流程状态: " + entityState);
        }
        return this.getStateDesc();
    }

    public String approvalResult() {
        if (this.stateCode == Reimburse.STATE_APPROVING || this.stateCode == Reimburse.STATE_END) {
            this.approvalResult = APPROVAL_RESULT_NONE;
        } else if (this.stateCode == Reimburse.STATE_REJECT) {
            this.approvalResult = APPROVAL_RESULT_REJECT;
        } else {

        }
        return this.approvalResult;
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
//        this.setStartUserid(task.getStartUserid());
//        this.setStartUsername(task.getStartUsername());
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
        vo.setRbsType(entity.getRbsType());
        vo.setStateCode(entity.getState());
        vo.setStateDesc(vo.stateDesc(entity.getState()));
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
