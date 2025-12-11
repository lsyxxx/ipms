package com.abt.market.entity;

import com.abt.testing.entity.Entrust;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;

/**
 * 结算单-关联检测项目
 */
@Table(name = "stlm_test",  indexes = {
        @Index(name = "idx_mid", columnList = "m_id"),
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestItem {

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
    @Column(name="entrust_id", columnDefinition="VARCHAR(50)")
    private String entrustId;

    /**
     * 样品编号（我方）
     */
    @NotNull(message = "样品编号不能为空")
    @Column(name="sample_no")
    private String sampleNo;

    /**
     * 检测单项id
     */
    @Column(name="check_module_id")
    private String checkModuleId;
    /**
     * 检测项目名称
     */
    @Column(name="check_module_name", columnDefinition="VARCHAR(100)")
    private String checkModuleName;

    /**
     * 样品数量单位
     */
    @Column(name="sample_unit", columnDefinition="VARCHAR(12)")
    private String sampleUnit;
    /**
     * 检测项目单价
     */
    @Column(name="price_", columnDefinition="DECIMAL(10,3)")
    private BigDecimal price;

    /**
     * 甲方样品编号
     */
    @Column(name="old_sample_no")
    private String oldSampleNo;

    /**
     * 井号
     */
    @Column(name="well_no")
    private String wellNo;

    /**
     * 关联委托单
     */
    @Transient
    private Entrust entrust;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "m_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
    private SettlementMain main;

    public TestItem(String entrustId, String sampleNo, String checkModuleId, String checkModuleName, String sampleUnit, BigDecimal price, String oldSampleNo, String wellNo) {
        this.entrustId = entrustId;
        this.sampleNo = sampleNo;
        this.checkModuleId = checkModuleId;
        this.checkModuleName = checkModuleName;
        this.sampleUnit = sampleUnit;
        this.price = price;
        this.oldSampleNo = oldSampleNo;
        this.wellNo = wellNo;
    }
}
