package com.abt.market.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class SettlementRequestForm extends RequestForm {
    /**
     * 结算客户名称
     */
    private String clientName;

    private String clientId;

    /**
     * 结算总金额
     */
    private String totalAmount;

    /**
     * 项目编号/检测编号/检测名称模糊查询
     */
    private String testLike;

}
