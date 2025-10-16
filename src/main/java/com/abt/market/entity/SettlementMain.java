package com.abt.market.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.abt.market.model.SaveType;
import com.abt.market.model.SettlementRelationType;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.util.SystemFileListConverter;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.repository.InvoiceApplyRepository;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;


/**
 * 结算单
 */
@Table(name = "stlm_main")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "SettlementMain.withTests", attributeNodes = @NamedAttributeNode("testItems")),
        @NamedEntityGraph(name = "SettlementMain.withExpenses", attributeNodes = @NamedAttributeNode("expenseItems")),
        @NamedEntityGraph(name = "SettlementMain.withRelations", attributeNodes = @NamedAttributeNode("relations")),
        @NamedEntityGraph(name = "SettlementMain.withSummaryTab", attributeNodes = @NamedAttributeNode("summaryTab")),
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(CommonJpaAuditListener.class)
public class SettlementMain extends AuditInfo implements CommonJpaAudit {

    /**
     * 结算单号
     */
    @Id
    @GeneratedValue(generator = "settlementIdGenerator")
    @GenericGenerator(name = "settlementIdGenerator", type = com.abt.market.SettlementIdGenerator.class)
    private String id;

    /**
     * 结算客户id
     */
    @Column(name="client_id")
    private String clientId;
    /**
     * 结算客户name
     */
    @Column(name="client_name")
    private String clientName;


    ///--  不是结算客户信息，因为开始写错了

    /**
     * 公司名称
     */
    @Column(name="company_name")
    private String companyName;
    /**
     * 公司名称
     */
    @Column(name="tax_no", columnDefinition="VARCHAR(128)")
    private String taxNo;
    /**
     * 公司名称
     */
    @Column(name="tel_no", columnDefinition="VARCHAR(32)")
    private String telephoneNo;

    /**
     * 公司名称
     */
    @Column(name="acc_bank", columnDefinition="VARCHAR(128)")
    private String accountBank;

    /**
     * 公司名称
     */
    @Column(name="acc_no", columnDefinition="VARCHAR(128)")
    private String accountNo;

    //-------

    /**
     * 是否含税
     * 是：表示输入的单价是含税价
     * 否：输入的单价不含税
     */
    @Column(name="is_tax", columnDefinition = "BIT")
    private boolean isTax = true;

    /**
     * 税率
     */
    @Column(name="tax_rate", columnDefinition = "DECIMAL(6,3)")
    private Double taxRate;

    /**
     * 检测项目合计（优惠前）
     */
    @Column(name="gross_test_amt", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal grossTestAmount;

    /**
     * 检测项目合计金额（优惠后）
     */
    @Column(name="net_test_amount", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal netTestAmount = BigDecimal.ZERO;

    /**
     * 其他费用合计金额
     */
    @Column(name="expense_amt", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal expenseAmount;

    /**
     * 优惠百分比，仅针对检测项目的费用优惠，不包含其他项目费用的
     */
    @Column(name="discount_perc", columnDefinition = "DECIMAL(6,3)")
    private Double discountPercentage;

    /**
     * 优惠金额，仅针对检测项目的费用优惠，不包含其他项目费用的
     */
    @Column(name="discount_amt", columnDefinition = "DECIMAL(10,2)")
    private Double discountAmount;

    /**
     * 本次结算总金额(不含税)
     */
    @Column(name="total_amt", columnDefinition="DECIMAL(10,2)")
    private BigDecimal totalAmount;

    /**
     * 含税价=totalAmount*(1+税率)
     */
    @Column(name="tax_amt", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal taxAmount;

    /**
     * 保存类型（暂存/保存）
     */
    @Enumerated(EnumType.STRING)
    @Column(name="save_type", columnDefinition="VARCHAR(16)")
    private SaveType saveType = SaveType.TEMP;

    @Column(name="remark_", length = 1000)
    private String remark;

    /**
     * 是否开票
     */
    @Column(name="is_issue_inv", columnDefinition = "BIT")
    private boolean isIssueInvoice = false;

    @Column(name="attachments", columnDefinition = "TEXT")
    @Convert(converter = SystemFileListConverter.class)
    private List<SystemFile> attachments;

    /**
     * 相关结算单据，比如盖章后的结算单
     */
    @Column(name="stlm_doc", columnDefinition = "TEXT")
    @Convert(converter = SystemFileListConverter.class)
    private List<SystemFile> settlementDocs;

    /**
     * 作废原因
     */
    @Column(name="invalid_reason", length = 500)
    private String invalidReason;

    /**
     * 选择模式
     * sample: 直接选择样品
     * entrust: 选择项目，自动按编号大小顺序分配样品（小-大）
     */
    @Column(name="mode_", length = 32)
    private String mode;

    /**
     * 关联结算检测项目
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "main")
    @OrderBy("sampleNo ASC")
    private Set<TestItem> testItems;

    /**
     * 关联的其他费用
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "main")
    @OrderBy("sortNo asc")
    private Set<ExpenseItem> expenseItems;

    /**
     * 关联的业务单据
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "main")
    private Set<SettlementRelation> relations;

    /**
     * 关联汇总表
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "main")
    @OrderBy("sortNo asc")
    private Set<SettlementSummary> summaryTab;

    /**
     * 关联合同
     */
    @Transient
    private List<SaleAgreement> saleAgreements;

    /**
     * 关联的发票
     */
    @Transient
    private List<InvoiceApply> invoiceApply;

    /**
     * 最终结算金额
     * @return  含税，返回含税金额(taxAmount)；不含税，返回totalAmount
     */
    public BigDecimal getFinalAmount() {
        return this.totalAmount;
    }

    /**
     * 计算检测项目合计金额
     */
    public void calculateGrossTestAmount() {
        if (testItems == null || testItems.isEmpty()) {
            this.grossTestAmount = BigDecimal.ZERO;
            return;
        }
        this.grossTestAmount = testItems.stream()
                .map(TestItem::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算优惠金额，仅对检测项目费用优惠
     */
    public void calculateDiscountAmount() {
        if (this.discountPercentage != null && this.grossTestAmount != null) {
            this.discountAmount = this.grossTestAmount.multiply(BigDecimal.valueOf(this.discountPercentage)).doubleValue();
        } else {
            this.discountAmount = 0.00;
        }
    }

    /**
     * 计算其他费用合计
     */
    public void calculateExpenseAmount() {
        if (expenseItems == null || expenseItems.isEmpty()) {
            this.expenseAmount = BigDecimal.ZERO;
            return;
        }
        this.expenseAmount = expenseItems.stream().map(ExpenseItem::getAmount).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public void calculateAllAmount() {
        this.calculateTotalAmount();
    }

    public void calculateTestNetAmount() {
        if (this.grossTestAmount != null && this.discountAmount != null) {
            this.netTestAmount = this.grossTestAmount.subtract(BigDecimal.valueOf(this.discountAmount));
        } else {
            this.netTestAmount = BigDecimal.ZERO;
        }
    }

    /**
     * 计算最终金额
     * 1. 检测总费用：检测项目*单价
     * 2. 优惠金额：检测总费用*优惠
     * 3. 实际检测费用：检测总费用-优惠
     * 4. 其他费用
     * 5. 最终金额=实际检测费用+其他费用
     */
    public void calculateTotalAmount() {
        calculateGrossTestAmount();
        calculateDiscountAmount();
        calculateTestNetAmount();
        calculateExpenseAmount();

        this.totalAmount = this.netTestAmount.add(this.expenseAmount);

    }

    /**
     * 获取关联的合同ID列表
     */
    public List<String> getAgreementIds() {
        if (relations == null) {
            return List.of();
        }
        return relations.stream()
                .filter(i -> SettlementRelationType.AGREEMENT.equals(i.getBizType()))
                .map(SettlementRelation::getRid)
                .distinct()
                .toList();
    }

    @Override
    public String toString() {
        return "SettlementMain{" +
                "id='" + id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientName='" + clientName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", taxNo='" + taxNo + '\'' +
                ", telephoneNo='" + telephoneNo + '\'' +
                ", accountBank='" + accountBank + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", isTax=" + isTax +
                ", taxRate=" + taxRate +
                ", grossTestAmount=" + grossTestAmount +
                ", netTestAmount=" + netTestAmount +
                ", expenseAmount=" + expenseAmount +
                ", discountPercentage=" + discountPercentage +
                ", discountAmount=" + discountAmount +
                ", totalAmount=" + totalAmount +
                ", saveType=" + saveType +
                ", remark='" + remark + '\'' +
                "} " + super.toString();
    }

    public boolean isEntrustMode() {
        return "entrust".equalsIgnoreCase(mode);
    }
}
