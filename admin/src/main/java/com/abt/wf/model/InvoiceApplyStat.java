package com.abt.wf.model;

import lombok.*;

/**
 * 开票统计
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class InvoiceApplyStat {

    private String project;
    private String clientId;
    private String clientName;
    private String testNo;
    private String contractName;
    private String company;

    /**
     * 开票数量
     */
    private Long invoiceCount;
    /**
     * 开票金额合计
     */
    private Double paying;

    public InvoiceApplyStat(String project, String clientId, String clientName, String testNo, String contractName, String company, Long invoiceCount, Double paying) {
        this.project = project;
        this.clientId = clientId;
        this.clientName = clientName;
        this.testNo = testNo;
        this.contractName = contractName;
        this.company = company;
        this.invoiceCount = invoiceCount;
        this.paying = paying;
    }
}
