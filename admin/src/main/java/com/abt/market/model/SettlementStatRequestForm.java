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
     * 项目名称，支持模糊查询。
     */
    private String projectName;

    /**
     * 客户名称，支持模糊查询。
     */
    private String clientName;

    /**
     * 合同查询关键字，同时匹配合同编号和合同名称。
     */
    private String contractQuery;

    /**
     * 结算状态，可选：SETTLED / PARTIALLY_SETTLED / UNSETTLED。
     */
    private String settlementStatus;

    /**
     * 年份。
     */
    private Integer year;

    /**
     * 日期快捷范围：
     * THIS_MONTH / THIS_QUARTER / THIS_YEAR / CUSTOM
     */
    private String datePreset;
}
