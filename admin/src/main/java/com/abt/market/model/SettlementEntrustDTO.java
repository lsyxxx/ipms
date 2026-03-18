package com.abt.market.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 项目结算详情
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettlementEntrustDTO {
    /**
     * 项目编号
     */
    private String entrustId;

    /**
     * 项目委托客户id
     */
    private String clientId;

    /**
     * 项目委托客户Name
     */
    private String clientName;

    /**
     * 项目总样品数量
     */
    private Double totalCount;

    /**
     * 已结算样品数量
     * TODO: 已结样品数量=项目总样品数量，不能表示结算完了，因为必须是每个样品的每个检测项目都结算了，才能算结算完成
     */
    private Double settledCount;

    /**
     * 未结算数量
     */
    private Double unsettledCount;

    /**
     * 已结算金额
     */
    private Double settledAmount;

    /**
     * 已开票金额
     */
    private Double invoiceAmount;

    /**
     * 已回款金额
     */
    private Double receivePaymentAmount;

}
