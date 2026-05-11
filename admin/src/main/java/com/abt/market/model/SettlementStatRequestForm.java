package com.abt.market.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

/**
 * 结算统计查询参数。
 */
@Getter
@Setter
public class SettlementStatRequestForm extends RequestForm {
    /**
     * 项目编号，支持模糊查询。
     */
    private String entrustId;

    /**
     * 客户名称，支持模糊查询。
     */
    private String clientName;

    /**
     * 合同查询关键字，同时匹配合同编号和合同名称。
     */
    private String contractQuery;

    /**
     * 结算状态，可选：SETTLED / UNSETTLED。
     */
    private String settlementStatus;
}
