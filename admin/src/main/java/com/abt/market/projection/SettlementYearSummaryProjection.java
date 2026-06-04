package com.abt.market.projection;

import java.math.BigDecimal;

public interface SettlementYearSummaryProjection {
    Integer getYear();

    Long getSettledEntrustCount();

    Long getPartialSettledEntrustCount();

    Long getUnsettledEntrustCount();

    BigDecimal getSettledAmount();
}
