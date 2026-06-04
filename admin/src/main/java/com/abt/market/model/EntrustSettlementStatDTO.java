package com.abt.market.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 按项目统计结算状态。
 */
@Getter
@Setter
@NoArgsConstructor
public class EntrustSettlementStatDTO {
    private String entrustId;
    private String projectName;
    private String clientName;
    private Long totalCount;
    private Long settledCount;
    private Long unsettledCount;
    private Long extraSettledCount;
    private BigDecimal settledAmount;
    private SettlementStatStatus settlementStatus;
}
