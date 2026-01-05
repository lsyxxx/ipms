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
     * 检测项目id，非必须
     */
    @Column(name="check_module_id")
    private String checkModuleId;

    /**
     * 检测项目名称，不一定是系统内配置的检测项目
     */
    @Column(name="check_module_name")
    private String checkModuleName;

    /**
     * 我方实际检测样品数量（等于系统内样品数量）
     */
    @Column(name="test_num", columnDefinition = "smallint")
    private Integer testNum;

    /**
     * 最终结算数量(不等于系统内录入样品数量)=甲方认定数量
     */
    @Column(name="sample_num", columnDefinition = "smallint")
    private int sampleNum;

    /**
     * 单价
     */
    @Column(name="price_", columnDefinition = "decimal(10,2)")
    private Double price;

//    /**
//     * 扣款费用
//     * 扣款费用=单价*最终结算数量
//     * 甲方未认定数量=系统内数量(testNum)-最终结算数量(sampleNum)
//     */
//    @Column(name="deduction_amt", columnDefinition = "decimal(10,2)")
//    private Double deductionAmount;

    /**
     * 合计金额
     */
    @Column(name="amt_", columnDefinition = "decimal(10,2)")
    private Double amount;

    /**
     * 单位
     */
    @Column(name="unit_")
    private String unit;

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

    public SettlementSummary(String entrustId, String checkModuleId, String checkModuleName, long sampleNum, Double amount) {
        this.entrustId = entrustId;
        this.checkModuleId = checkModuleId;
        this.checkModuleName = checkModuleName;
        this.sampleNum = Integer.parseInt(sampleNum + "");
        this.amount = amount;
    }

    public SettlementSummary(String entrustId, String checkModuleId, String checkModuleName, Double price, long sampleNum, Double amount) {
        this.entrustId = entrustId;
        this.checkModuleId = checkModuleId;
        this.checkModuleName = checkModuleName;
        this.price = price;
        this.sampleNum = Integer.parseInt(sampleNum + "");
        this.amount = amount;
    }


    public static SettlementSummary from(StlmSmryTemp temp, String mid) {
        SettlementSummary smry = new SettlementSummary();
        if (temp != null) {
            smry.setMid(mid);
            smry.setEntrustId(temp.getEntrustId());
            smry.setCheckModuleId(temp.getCheckModuleId());
            smry.setCheckModuleName(temp.getCheckModuleName());
            smry.setSampleNum(temp.getSampleNum());
            smry.setPrice(temp.getPrice().doubleValue());
            smry.setAmount(temp.getAmount().doubleValue());
            smry.setUnit(temp.getUnit());
            smry.setRemark(temp.getRemark());
            smry.setSortNo(temp.getSortNo());
        }
        return smry;
    }

}
