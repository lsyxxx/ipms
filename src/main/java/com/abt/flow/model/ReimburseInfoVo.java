package com.abt.flow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 我的报销
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class ReimburseInfoVo extends FlowInfoVo {

    private int voucherNum;
    private double cost;
    private String reason;
}
