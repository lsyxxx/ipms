package com.abt.finance.entity;

import com.abt.wf.entity.InvoiceApply;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


/**
 * 回款登记关联表
 */
@Table(name = "fi_rec_pay_ref")
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


    @ManyToOne
    @JoinColumn(name = "rp_id", referencedColumnName = "id")
    @JsonIgnore
    private ReceivePayment receivePayment;

    /**
     * 关联发票/业务id
     */
    @Column(name="ref_id")
    private String refId;

    /**
     * 关联内容
     */
    @Column(name="ref_content", length = 1000)
    private String refContent;

    /**
     * 关联类型，发票/如合同/结算单
     */
    private String type = TYPE_INVOICE;


    /**
     * 合同
     */
    public static final String TYPE_CONTRACT = "contract";
    /**
     * 结算单
     */
    public static final String TYPE_SETTLEMENT = "settlement";

    /**
     * 开票信息
     */
    public static final String TYPE_INVOICE = "invoice";


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    private InvoiceApply invoiceApply;



}
