package com.abt.market.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "stlm_smry_temp", indexes = {
        @Index(name = "mid", columnList = "m_id"),
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StlmSmryTemp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Size(max = 255)
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "amt_", precision = 12, scale = 3)
    private BigDecimal amount;

    @Size(max = 255)
    @Column(name = "check_module_id")
    private String checkModuleId;

    @Size(max = 255)
    @Column(name = "check_module_name")
    private String checkModuleName;

    @Size(max = 255)
    @Column(name = "entrust_id")
    private String entrustId;

    @Size(max = 255)
    @Column(name = "m_id")
    private String mid;

    @Column(name = "price_", precision = 12, scale = 3)
    private BigDecimal price;

    @Size(max = 512, message = "备注信息不能超过100字")
    @Column(name = "remark_", length = 512)
    private String remark;

    @Column(name = "sample_num")
    private Integer sampleNum;


//    /**
//     * 扣款费用
//     * 扣款费用=单价*最终结算数量
//     * 甲方未认定数量=系统内数量(testNum)-最终结算数量(sampleNum)
//     */
//    @Column(name="deduction_amt", columnDefinition = "decimal(10,2)")
//    private Double deductionAmount;

    /**
     * 我方实际检测样品数量（等于系统内样品数量）
     */
    @Column(name="test_num", columnDefinition = "smallint")
    private Integer testNum;


    @Column(name = "sort_no")
    private Integer sortNo;

    @Size(max = 255)
    @Column(name = "unit_")
    private String unit;

}