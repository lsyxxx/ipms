package com.abt.wf.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 款项支付单搜索参数
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PayVoucherRequestForm extends RequestForm {
    /**
     * 合同名称
     */
    private String contactName;
    /**
     * 合同编号
     */
    private String contactNo;
}
