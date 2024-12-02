package com.abt.finance.service;

import com.abt.finance.entity.AccountItem;
import com.abt.finance.entity.BankAccount;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    void setPayBankAccount(BankAccount payBankAccount);

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
    void setTaxItem(AccountItem taxItem);

    /**
     * 核算科目id
     */
    void setAccountItemId(String accountItemId);
    String getAccountItemId();
    void setAccountItem(AccountItem accountItem);

    String getBusinessId();

    /**
     * 业务归属
     */
    String getCompany();

    /**
     * 事由
     */
    String getReason();

    /**
     * 经办人姓名
     */
    String getUsername();

    /**
     * 费用
     */
    Double getExpense();

    /**
     * 收款人名称
     */
    String getReceiveUser();

    String getDepartmentId();
    String getDepartmentName();
    String getTeamId();
    String getTeamName();

    String getFileJson();
    LocalDateTime getBizCreateDate();
    int getVoucherNum();

    default void clearData() {
        setPayDate(null);
        setPayBankAccount(null);
        setPayType(null);
        setPayLevel(null);
        setPayAccountId(null);
        setAccountItem(null);
        setAccountItemId(null);
        setTaxItem(null);
        setTaxItemId(null);
    }

}
