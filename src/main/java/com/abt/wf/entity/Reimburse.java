package com.abt.wf.entity;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报销业务实体
 */
@Data
public class Reimburse {

    private double cost;
    private String reason;

    /**
     * 报销日期
     */
    private LocalDateTime rbsDate;
    private int voucherNum;


    //-- processDefinition
    private String processDefinitionKey;
    private String processDefinitionId;

    //-- processInstance
    private String processInstanceId;
    /**
     * 状态
     * 0：审批中
     * 1. 已拒绝(整个流程)
     * 2. 已通过(整个流程)
     */
    private int state;

    //-- start
    private String startUserId;
    private String startUserName;
    private LocalDateTime startTime;
    //-- end
    private String endTime;

}
