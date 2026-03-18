package com.abt.market.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 结算单-销售合同关联dto
 */
@Getter
@Setter
public class SettlementAgreementDTO {

    /**
     * 结算单号
     */
    private String settlementId;

    /**
     * 结算金额
     */
    private BigDecimal totalAmount;

    /**
     * 结算单位
     */
    private String clientName;

    /**
     * 结算备注
     */
    private String remark;

    /**
     * 结算人
     */
    private String settlementUser;

    /**
     * 结算时间
     */
    private LocalDateTime settlementDate;

    /**
     * 结算单状态
     */
    private SaveType saveType;



    public SettlementAgreementDTO(String settlementId, BigDecimal totalAmount, String clientName, String remark, String settlementUser, LocalDateTime settlementDate, SaveType saveType) {
        this.settlementId = settlementId;
        this.totalAmount = totalAmount;
        this.clientName = clientName;
        this.remark = remark;
        this.settlementUser = settlementUser;
        this.settlementDate = settlementDate;
        this.saveType = saveType;
    }

    @Override
    public String toString() {
        return "SettlementAgreementDTO{" +
                "settlementId='" + settlementId + '\'' +
                ", totalAmount=" + totalAmount +
                ", clientName='" + clientName + '\'' +
                '}';
    }
}
