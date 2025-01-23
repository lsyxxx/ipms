package com.abt.market.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.abt.sys.model.entity.CustomerInfo;
import com.abt.testing.entity.Entrust;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


/**
 * 结算单
 */
@Table(name = "mkt_settlement_main")
@NamedEntityGraphs({
        @NamedEntityGraph(name = "SettlementMain.withItems", attributeNodes = @NamedAttributeNode("settlementItems")),
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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 业务单号
     */
    @Column(name="code_")
    private String code;


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

    /**
     * 收款公司名称
     */
    @Column(name="com_name")
    private String companyName;

    /**
     * 收款公司代码（ABT/GRD/DC）
     */
    @Column(name="com_code", columnDefinition="VARCHAR(12)")
    private String companyCode;
    /**
     * 收款公司税号
     */
    @Column(name="tax_no", columnDefinition="VARCHAR(128)")
    private String taxNo;
    /**
     * 收款公司电话
     */
    @Column(name="tel_no", columnDefinition="VARCHAR(32)")
    private String telephoneNo;

    /**
     * 收款公司开户行
     */
    @Column(name="acc_bank", columnDefinition="VARCHAR(128)")
    private String accountBank;

    /**
     * 收款公司账户
     */
    @Column(name="acc_no", columnDefinition="VARCHAR(128)")
    private String accountNo;

    /**
     * 本次结算总金额
     */
    @Column(name="sum_", columnDefinition="DECIMAL(10,2)")
    private Double sum;

    /**
     * 是否含税
     */
    @Column(name="is_tax", columnDefinition = "BIT")
    private boolean isTax = true;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "main")
    List<SettlementItem> settlementItems;

    /**
     * 关联项目
     */
    @Transient
    private List<Entrust> entrustList;




}
