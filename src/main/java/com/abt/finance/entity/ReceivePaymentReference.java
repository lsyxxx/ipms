package com.abt.finance.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.number.PercentStyleFormatter;

/**
 * 回款登记关联表
 */
@Table(name = "rec_payment_ref")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceivePaymentReference{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * ReceivePayment id
     */
    @Column(name="rec_payment_id")
    @NotNull
    private String receivePaymentId;

    /**
     * 关联id
     */
    @NotNull
    @Column(name="ref_id")
    private String refId;

    /**
     * 关联类型，如合同/结算单
     */
    private String type;

    /**
     * 合同
     */
    public static final String TYPE_CONTRACT = "contract";
    /**
     * 结算单
     */
    public static final String TYPE_SETTLEMENT = "settlement";

}
