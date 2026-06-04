package com.abt.market.projection;

import java.math.BigDecimal;

public interface EntrustSettlementStatProjection {
    String getEntrustId();

    String getProjectName();

    String getClientName();

    Long getTotalCount();

    Long getSettledCount();

    Long getUnsettledCount();

    Long getExtraSettledCount();

    BigDecimal getSettledAmount();

    String getSettlementStatus();
}
