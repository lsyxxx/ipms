package com.abt.finance.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

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

}
