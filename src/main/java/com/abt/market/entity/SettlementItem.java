package com.abt.market.entity;

import com.abt.testing.entity.Entrust;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

/**
 * 结算单-结算项目
 */
@Table(name = "mkt_settlement_item",  indexes = {
        @Index(name = "idx_mid", columnList = "m_id"),
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettlementItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 关联主表id
     */
    @Column(name="m_id")
    private String mid;

    /**
     * 委托单号/检测编号
     */
    @Column(name="entrust_no", columnDefinition="VARCHAR(50)")
    private String entrustNo;

    /**
     * 检测单项id
     */
    private String moduleId;
    /**
     * 检测项目名称
     */
    @Column(name="test_name", columnDefinition="VARCHAR(100)")
    private String moduleName;
    /**
     * 样品数量
     */
    @Column(name="sample_num", columnDefinition="SMALLINT")
    private Long sampleNum;
    /**
     * 样品数量单位
     */
    @Column(name="sample_unit", columnDefinition="VARCHAR(12)")
    private String sampleUnit;
    /**
     * 检测项目单价
     */
    @Column(name="price_", columnDefinition="DECIMAL(10,2)")
    private Double price;

    /**
     * 检测项目合计金额
     */
    @Column(name="sum_", columnDefinition = "DECIMAL(10,2)")
    private Double sum;


    /**
     * 已结算的样品数量
     */
    @Column(name="finished_num", columnDefinition="SMALLINT")
    private Integer settledNum = 0;

    /**
     * 关联委托单
     */
    @Transient
    private Entrust entrust;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "m_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    private SettlementMain main;

    public SettlementItem(String moduleId, String moduleName, Long sampleNum) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.sampleNum = sampleNum;
    }

}
