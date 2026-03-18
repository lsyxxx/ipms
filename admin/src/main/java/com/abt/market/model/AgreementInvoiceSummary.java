package com.abt.market.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 单合同开票统计
 */
@Getter
@Setter
public class AgreementInvoiceSummary {

    /**
     * 合同id
     */
    private String agreementId;

    /**
     * 合计开票金额金额
     */
    private Double invoiceSum;

    /**
     * 流程类型-通过、审批中、全部
     */
    private String processType;

    public AgreementInvoiceSummary() {
        super();
    }
    
    public AgreementInvoiceSummary(String agreementId, Double invoiceSum, String processType) {
        this.agreementId = agreementId;
        this.invoiceSum = invoiceSum;
        this.processType = processType;
    }


}
