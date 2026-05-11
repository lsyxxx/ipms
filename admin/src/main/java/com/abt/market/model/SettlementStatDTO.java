package com.abt.market.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 结算汇总查询结果。
 */
@Getter
@Setter
@NoArgsConstructor
public class SettlementStatDTO {
    /**
     * 项目编号
     */
    private String entrustId;

    /**
     * 客户id
     */
    private String clientId;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 是否已查到有效结算
     */
    private boolean settled;

    /**
     * 结算状态
     */
    private SettlementStatStatus settlementStatus;

    /**
     * 有效结算单数量
     */
    private Long settlementCount;

    /**
     * 有效结算金额合计
     */
    private BigDecimal settledAmount;

    public SettlementStatDTO(String entrustId, String clientId, String clientName, String contractNo,
                             Long settlementCount, BigDecimal settledAmount) {
        this.entrustId = entrustId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.contractNo = contractNo;
        this.settlementCount = settlementCount;
        this.settledAmount = settledAmount;
    }

    public SettlementStatDTO(String entrustId, String clientId, String clientName, String contractNo,
                             String projectName, String contractName,
                             Long settlementCount, BigDecimal settledAmount) {
        this.entrustId = entrustId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.contractNo = contractNo;
        this.projectName = projectName;
        this.contractName = contractName;
        this.settlementCount = settlementCount;
        this.settledAmount = settledAmount;
    }

    public SettlementStatDTO(String entrustId, String clientId, String clientName, String contractNo,
                             Number settlementCount, Number settledAmount) {
        this.entrustId = entrustId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.contractNo = contractNo;
        this.settlementCount = toLong(settlementCount);
        this.settledAmount = toBigDecimal(settledAmount);
    }

    public SettlementStatDTO(String entrustId, String clientId, String clientName, String contractNo,
                             String projectName, String contractName,
                             Number settlementCount, Number settledAmount) {
        this.entrustId = entrustId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.contractNo = contractNo;
        this.projectName = projectName;
        this.contractName = contractName;
        this.settlementCount = toLong(settlementCount);
        this.settledAmount = toBigDecimal(settledAmount);
    }

    private Long toLong(Number value) {
        return value == null ? 0L : value.longValue();
    }

    private BigDecimal toBigDecimal(Number value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }
        return new BigDecimal(Objects.toString(value));
    }
}
