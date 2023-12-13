package com.abt.wf.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 报销申请表单
 */
@Data
public class ReimburseApplyForm {
    @DecimalMin(value = "0.00", message = "报销金额不能小于0.00")
    private double cost;
    private String reason;

    /**
     * 报销日期
     */
    @NotNull
    private LocalDateTime rbsDate;

    //-- processDefinition
    private String processDefinitionKey;
    private String processDefinitionId;

    //-- submit
    private String userid;
    private String username;

    /**
     * 预览生成的流程实例的id
     */
    private String previewInstanceId;

    /**
     * 票据数量
     */
    @Max(value = 99, message = "票据数量不能超过99")
    private int voucherNum;

    private boolean isLeader = false;

    public Map<String, Object> variableMap = new HashMap<>();

    public Map<String, Object> variableMap() {
        this.variableMap.clear();
        this.variableMap.put("cost", cost);
        this.variableMap.put("isLeader", isLeader);
        return this.variableMap;
    }




}
