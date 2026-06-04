package com.abt.market.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 年度结算统计汇总。
 */
@Getter
@Setter
@NoArgsConstructor
public class SettlementYearSummaryDTO {
    private Integer year;
    private Long settledEntrustCount;
    private Long partialSettledEntrustCount;
    private Long unsettledEntrustCount;
    private BigDecimal settledAmount;
}
