package com.abt.flow.model;

import lombok.Data;

/**
 * 我的报销
 */
@Data
public class ReimburseInfoVo extends FlowInfoVo {

    private int voucherNum;
    private double cost;
    private String reason;
}
