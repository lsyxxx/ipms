package com.abt.finance.service;

import java.time.LocalDate;

/**
 * 资金流出接口
 */
public interface ICreditBook {
    /**
     * 付款级别
     */
    void setPayLevel(String payLevel);
    String getPayLevel();

    /**
     * 支付方式
     */
    void setPayType(String payType);
    String getPayType();

    /**
     * 付款账号
     */
    void setPayAccountId(String payAccountId);
    String getPayAccountId();

    /**
     * 付款时间
     */
    void setPayDate(LocalDate payDate);
    LocalDate getPayDate();

    /**
     * 税务科目id
     */
    void setTaxItemId(String taxItemId);
    String getTaxItemId();

    /**
     * 核算科目id
     */
    void setAccountItemId(String accountItemId);
    String getAccountItemId();

    /**
     * 确认项目
     */
    void setCheckItemJson(String checkItemJson);
    String getCheckItemJson();
}
