package com.abt.market.projection;

import java.time.LocalDateTime;

public interface EntrustSettlementDiffProjection {
    String getDiffType();

    String getEntrustId();

    String getSampleRegistId();

    String getSampleNo();

    String getCheckModeuleId();

    String getCheckModeuleName();

    String getTestItemId();

    String getSettlementMainId();

    String getSettlementSaveType();

    LocalDateTime getSettlementCreateDate();

    String getClientName();
}
