package com.abt.market.entity;

import com.abt.common.config.ValidateGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

/**
 * 结算汇总表-主表
 * 用于保存样品汇总表，方便查看
 */
@Table(name = "stlm_smry")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettlementSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联settlementMain id
     */
    @Column(name="m_id")
    private String mid;

    /**
     * 委托单编号
     */
    @Column(name="entrust_id")
    private String entrustId;

    /**
     * 行序号
     */
    @Column(name="sort_no", columnDefinition = "smallint")
    private int sortNo;

    /**
     * 检测项目id
     */
    @Column(name="check_module_id")
    private String checkModuleId;

    /**
     * 检测项目名称
     */
    @Column(name="check_module_name")
    private String checkModuleName;

    /**
     * 样品数量
     */
    @Column(name="sample_num", columnDefinition = "smallint")
    private int sampleNum;

    /**
     * 单价
     */
    @Column(name="price_", columnDefinition = "decimal(10,2)")
    private Double price;

    /**
     * 合计金额
     */
    @Column(name="amt_", columnDefinition = "decimal(10,2)")
    private Double amount;

    /**
     * 备注
     */
    @Size(message = "备注信息不能超过100字", groups = ValidateGroup.Save.class)
    @Column(name="remark_", length = 512)
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "m_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    private SettlementMain main;



}
