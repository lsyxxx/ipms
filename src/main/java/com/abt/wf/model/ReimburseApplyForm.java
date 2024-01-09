package com.abt.wf.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rbsDate;
    /**
     * 票据数量
     */
    @Max(value = 99, message = "票据数量不能超过99")
    private int voucherNum;
    /**
     * 报销类型
     */
    private String rbsType;

    //-- processDefinition
    private String processDefinitionKey;
    private String processDefinitionId;

    //-- submit: apply/approve
    private String userid;
    private String username;
    private List<String> managerList;

    /**
     * 预览生成的流程实例的id
     */
    private String previewInstanceId;

    private String processInstanceId;

    /**
     * 业务实例id
     */
    private String entityId;


    private boolean isLeader = false;

    public Map<String, Object> variableMap = new HashMap<>();

    private String comment;
    private String decision;

    public static final String REJECT = "reject";
    public static final String PASS = "pass";
    public static final String KEY_COST = "cost";
    public static final String KEY_IS_LEADER = "isLeader";
    public static final String KEY_MANAGER_LIST = "managerList";
    public static final String KEY_STARTER = "starter";

    public Map<String, Object> variableMap() {
        this.variableMap.clear();
        this.variableMap.put(KEY_COST, cost);
        this.variableMap.put(KEY_IS_LEADER, isLeader);
        this.variableMap.put(KEY_MANAGER_LIST, managerList);
        this.variableMap.put(KEY_STARTER, userid);
        return this.variableMap;
    }

    public boolean isReject() {
        return REJECT.equals(this.decision);
    }

    public boolean isPass() {
        return PASS.equals(this.decision);
    }




}
