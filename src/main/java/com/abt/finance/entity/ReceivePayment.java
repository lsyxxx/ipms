package com.abt.finance.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import java.time.LocalDate;
import java.util.List;

import com.abt.common.model.User;
import com.abt.market.entity.SaleAgreement;
import com.abt.wf.entity.InvoiceApply;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 回款记录
 */
@Table(name = "fi_rec_payment")
@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(name = "ReceivePayment.withReference", attributeNodes = @NamedAttributeNode("references")),
})
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(AuditingEntityListener.class)
public class ReceivePayment extends AuditInfo {

    /**
     * 登记流水号
     */
    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    /**
     * 回款时间
     */
    @NotNull(message = "回款时间必填", groups = {ValidateGroup.Save.class})
    @Column(name="rec_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiveDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 回款金额
     */
    @NotNull(message = "回款金额必填", groups = {ValidateGroup.Save.class})
    @Column(name="amount_", columnDefinition = "decimal(18,2)")
    private Double amount;

    /**
     * 实际回款客户信息，可能和开票客户不一样，那么没有id
     * 如果一样，则带入开票客户
     */
    @Column(name="customer_id")
    private String customerId;

    @NotNull
    @Column(name="customer_name", columnDefinition = "VARCHAR(512)")
    private String customerName;

    /**
     * 银行账号
     */
    @NotNull(message = "客户银行账号必填", groups = {ValidateGroup.Save.class})
    @Column(name="bank_account")
    private String bankAccount;

    /**
     * 开户行
     */
    @NotNull(message = "客户开户行必填", groups = {ValidateGroup.Save.class})
    @Column(name="bank_name")
    private String bankName;

    /**
     * 税号
     */
    @NotNull
    @Column(name="tax_no")
    private String taxNo;



    /**
     * todo: 通知ids
     * json字符串: [{id: "", name: "username", code: "jobNumber"}, ]
     */
    @Lob
    @Column(name="notify_users")
    private String notifyStrings;

    /**
     * 通知用户
     */
    @Transient
    private List<User> notifyUsers;

    /**
     * 回款依据
     */
    @OneToMany(mappedBy = "receivePayment", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReceivePaymentReference> references;

    /**
     * 关联的发票
     * 非必须，可能汇款时还没开票
     */
    @Transient
    private List<InvoiceApply> invoices;

    /**
     * 关联合同
     * todo 未来根据发票关联合同
     */
    @Transient
    private List<SaleAgreement> saleAgreements;

    /**
     * 关联的结算单
     * todo 未来根据发票关联结算
     */
    @Transient
    private List<SettlementDocument> paymentDocs;


    //可能有的
    //我方接收账户，对方账户


}
