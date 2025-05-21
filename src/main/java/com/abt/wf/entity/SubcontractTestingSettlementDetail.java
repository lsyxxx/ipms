package com.abt.wf.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.math.BigDecimal;

/**
 * 外送检测结算详情
 */
@Table(name = "wf_sbct_stl_dtl",  indexes = {
        @Index(name = "idx_mid", columnList = "mid"),
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubcontractTestingSettlementDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 项目编号
     */
    @Column(name="entrust_id", nullable=false)
    private String entrustId;

    @Transient
    private String projectName;

    /**
     * 委托单位
     */
    @Transient
    private String entrustCompany;
    
    /**
     * 检测项目名称
     */
    @Column(name="check_module_name", nullable = false)
    private String checkModuleName;

    @Column(name="check_module_id")
    private String checkModuleId;

    /**
     * 外送数量
     */
    @Column(name="num_")
    private int num;

    /**
     * 单位
     */
    @Column(name="unit_")
    private String unit;

    /**
     * 单价
     */
    @Column(name="price_", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 总价
     */
    @Column(name="total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * 备注
     */
    @Column(name="remark_", columnDefinition = "VARCHAR(1000)")
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mid", referencedColumnName = "id")
    @NotFound(action= NotFoundAction.IGNORE)
    @JsonBackReference
    private SubcontractTestingSettlementMain main;

    public SubcontractTestingSettlementDetail(String entrustId, String checkModuleName, long num) {
        this.entrustId = entrustId;
        this.checkModuleName = checkModuleName;
        this.num = (int)num;
    }



}
