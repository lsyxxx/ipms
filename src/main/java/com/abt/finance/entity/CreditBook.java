package com.abt.finance.entity;

import com.abt.common.model.AuditInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

/**
 * 贷方记账，资金流出
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "fi_credit_book")
public class CreditBook extends AuditInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 记账对应服务名称，如费用报销
     */
    @Column(name="srv_name", columnDefinition="VARCHAR(128)")
    private String serviceName;

    /**
     * 金额
     */
    @Column(name="expense", columnDefinition="DECIMAL(10,2)")
    private double expense;

    /**
     * 费用类型
     */
    @Column(name="exp_type", columnDefinition="VARCHAR(128)")
    private String expenseType;
    /**
     * 票据数量
     */
    @Column(name="inv_num", columnDefinition="TINYINT")
    private int invoiceNum;

    /**
     * 票据类型
     */
    @Column(name="inv_type", columnDefinition="VARCHAR(128)")
    private String invoiceType;

    /**
     * 付款方式
     */
    @Column(name="pay_type", columnDefinition="VARCHAR(128)")
    private String payType;
    /**
     * 付款时间
     */
    @Column(name="pay_date")
    private LocalDateTime payDate;
    /**
     * 付款账号
     */
    @Column(name="pay_acc", columnDefinition="VARCHAR(128)")
    private String payAccount;

    /**
     * 付款账号银行
     */
    @Column(name="pay_bank", columnDefinition="VARCHAR(128)")
    private String payBank;

    /**
     * 收款账号
     */
    @Column(name="rec_acc", columnDefinition="VARCHAR(128)")
    private String receiveAccount;
    /**
     * 收款账号银行
     */
    @Column(name="rec_bank", columnDefinition="VARCHAR(128)")
    private String receiveBank;
    /**
     * 收款人(可能是对公账户）
     */
    @Column(name="rec_userid", columnDefinition="VARCHAR(128)")
    private String receiveUserid;

    /**
     * 业务详情url
     */
    @Column(name="biz_url", columnDefinition="VARCHAR(128)")
    private String businessUrl;
    /**
     * 出纳id
     */
    @Column(name="cashier_id", columnDefinition="VARCHAR(128)")
    private String cashier;
    /**
     * 出纳姓名
     */
    @Column(name="cashier_name", columnDefinition="VARCHAR(32)")
    private String cashierName;
    /**
     * 财务主管id
     */
    @Column(name="fi_manager_id", columnDefinition="VARCHAR(128)")
    private String financeManagerId;
    /**
     * 财务主管name
     */
    @Column(name="fi_manager_name", columnDefinition="VARCHAR(32)")
    private String financeManagerName;

    /**
     * 业务实体id
     */
    @Column(name="biz_id", columnDefinition="VARCHAR(128)")
    private String businessId;

    /**
     * 记账备注
     */
    @Column(name="remark_", columnDefinition="VARCHAR(1000)")
    private String remark;

    @Column(columnDefinition="VARCHAR(128)")
    private String project;


    /**
     * 业务简述
     */
    @Column(name="desc_", columnDefinition="VARCHAR(512)")
    private String desc;

    /**
     * 上传附件json
     */
    @Column(name="attchment", columnDefinition="VARCHAR(1000)")
    private String attachment;

    /**
     * 关联的附件
     */
    @Column(name="attachment_rel", columnDefinition="VARCHAR(1000)")
    private String relatedAttachment;


}

