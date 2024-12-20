package com.abt.finance.entity;

import com.abt.common.model.AuditInfo;
import java.time.LocalDate;
import java.util.List;

import com.abt.market.entity.SaleAgreement;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 回款记录
 */
@Table(name = "fi_rec_payment")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(AuditingEntityListener.class)
public class ReceivePayment extends AuditInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 业务编号
     */
    @NotNull
    @Column(name="code_", columnDefinition = "VARCHAR(128)")
    private String code;

    /**
     * 回款时间
     */
    @Column(name="rec_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDate;

    /**
     * 备注
     */
    private String remark;

    @NotNull
    private Double amount;

    /**
     * 客户id
     */
    @Column(name="customer_id")
    private String customerId;
    
    @Column(name="customer_name", columnDefinition = "VARCHAR(512)")
    private String customerName;

    /**
     * 回款依据
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rec_payment_ref",
            joinColumns = @JoinColumn(name = "rec_payment_id"),
            inverseJoinColumns = @JoinColumn(name = "ref_id")
    )
    private List<ReceivePaymentReference> references;

    /**
     * 关联合同
     */
    @Transient
    private List<SaleAgreement> saleAgreements;


//    /**
//     * 关联的结算单
//     */
//    private List settlement;

}
