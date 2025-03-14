package com.abt.finance.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.common.model.IToken;
import com.abt.common.service.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

/**
 * 发票
 */
@Table(name = "fi_invoice")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(CommonJpaAuditListener.class)
public class Invoice extends AuditInfo implements CommonJpaAudit {

    /**
     * 发票代码
     */
    @Id
    @NotNull(groups = {ValidateGroup.All.class}, message = "发票号码不能为空")
    private String id;

    /**
     * 发票号码
     */
    private String code;

    /**
     * 开票日期
     */
    @Column(name="issue_date")
    private LocalDate issueDate;

    /**
     * 发票类型
     */
    @Column(name="type_")
    private String type;

    /**
     * 商品名称
     */
    @Size(max = 1000)
    @Column(name="good_name", length = 1000)
    private String goodName;

    /**
     * 金额（不含税）
     */
    @Column(name="price_")
    private double price;

    /**
     * 税额
     */
    @Column(name="tax_")
    private double tax;

    /**
     * 价税合计
     */
    @Column(name="total_price")
    private double totalPrice;

    /**
     * 关联的单据编号（如费用报销审批编号）
     */
    @Column(name="ref_code")
    private String refCode;

    /**
     * 关联的单据的服务名称
     */
    @Column(name="ref_name")
    private String refName;

    /**
     * 购方名称
     */
    @Column(name="buyer_name")
    private String buyerName;


    /**
     * 销方名称
     */
    @Column(name="seller_name")
    private String sellerName;

    @Transient
    private String error;


}
