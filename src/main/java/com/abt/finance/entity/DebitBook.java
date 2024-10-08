package com.abt.finance.entity;

import com.abt.common.model.AuditInfo;
import com.abt.common.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 借记账，现金流入
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@DynamicUpdate
@DynamicInsert
@Entity
@NoArgsConstructor
@Table(name = "fi_debit_book")
public class DebitBook extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    /**
     * 关联业务id
     */
    @Column(name="biz_id", columnDefinition="VARCHAR(128)")
    @NotNull
    private String businessId;

    /**
     * 收入类别, Constants.CREDIT_BOOK_TYPE_*
     */
    @Column(name="type_", columnDefinition="VARCHAR(128)")
    @NotNull
    private String type;

    /**
     * 收入性质：Constants.CREDIT_BOOK_NATURE_*
     */
    @Column(name="nature_", columnDefinition="VARCHAR(128)")
    @NotNull
    private String nature;
    /**
     * 事由
     */
    @Column(name="reason_", columnDefinition="VARCHAR(1000)")
    private String reason;

    @Column(name="project_", columnDefinition="VARCHAR(512)")
    private String project;


    @Column(name="amount", columnDefinition="DECIMAL(10,2)")
    @NotNull
    private BigDecimal amount;

    @Column(name="inv_url", columnDefinition="VARCHAR(128)")
    private String invoiceUrl;
    
    @Column(name="pay_company", columnDefinition="VARCHAR(128)")
    private String payCompany;

    @Column(name="pay_bank", columnDefinition="VARCHAR(128)")
    private String payBank;
    @Column(name="pay_acc", columnDefinition="VARCHAR(128)")
    private String payAccount;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="pay_date")
    private LocalDateTime payDate;
    
    @Column(name="rec_bank", columnDefinition="VARCHAR(128)")
    private String receiveBank;

    @Column(name="rec_acct", columnDefinition="VARCHAR(128)")
    private String receiveAccount;

    @Column(name="officer_id", columnDefinition="VARCHAR(128)")
    private String officerId;

    @Column(name="officer_name", columnDefinition="VARCHAR(128)")
    private String officerName;
    @Column(name="remark_", columnDefinition="VARCHAR(1000)")
    private String remark;
    /**
     * 业务说明：Constants.BIZ_DESC_*
     */
    @Column(name="desc_", columnDefinition="VARCHAR(512)")
    private String description;

}
