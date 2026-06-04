package com.abt.market.projection;

import java.math.BigDecimal;

public interface EntrustSettlementExportProjection {
    String getEntrustId();

    String getContractNo();

    String getClientName();

    String getProjectName();

    Long getTotalCount();

    Long getSettledCount();

    Long getExtraSettledCount();

    BigDecimal getSettledAmount();

    String getSettlementIds();
}
