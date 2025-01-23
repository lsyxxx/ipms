package com.abt.finance.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 回款登记查询
 */
@Getter
@Setter
public class ReceivePaymentRequestForm extends RequestForm {

    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 发票申请单号
     */
    private String invoiceApplyId;

    /**
     * 通知人用户id
     */
    private String notifyUserid;

    /**
     * 回款金额
     */
    private BigDecimal amount;

    /**
     * 回款日期范围-开始
     */
    private LocalDate receiveDateStart;

    /**
     * 回款日期范围-结束
     */
    private LocalDate receiveDateEnd;

    /**
     * 项目名称
     */
    private String project;

    /**
     * 统计方式,
     * 必须是ReceivePayment的属性名，表示用该属性groupby
     * project, customerId
     */
    private List<String> statsType;


    private String contractName;

    /**
     * 检测编号
     */
    private String testNo;

    private List<String> company;




}
