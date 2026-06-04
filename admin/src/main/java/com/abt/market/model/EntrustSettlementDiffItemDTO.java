package com.abt.market.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 项目结算差异明细。
 */
@Getter
@Setter
@NoArgsConstructor
public class EntrustSettlementDiffItemDTO {
    private SettlementDiffType diffType;
    private String entrustId;
    private String sampleRegistId;
    private String sampleNo;
    private String checkModeuleId;
    private String checkModeuleName;
    private String testItemId;
    private String settlementMainId;
    private String settlementSaveType;
    private LocalDateTime settlementCreateDate;
    private String clientName;
}
