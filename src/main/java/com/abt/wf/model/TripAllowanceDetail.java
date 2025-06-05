package com.abt.wf.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 差旅报销-个人补助
 */
@Getter
@Setter
public class TripAllowanceDetail {
    /**
     * 补助人员id
     */
    private String userid;
    /**
     * 补助人员
     */
    private String username;

    /**
     * 补助金额（days天总金额）
     */
    private BigDecimal amount;
    /**
     * 补助天数
     */
    private double days;
}
