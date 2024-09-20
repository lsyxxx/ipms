package com.abt.finance.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.abt.sys.model.WithQuery;
import com.abt.sys.model.entity.Org;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


/**
 * 固定资产
 */

@Getter
@Setter
@Entity
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Table(name = "fi_fixed_asset")
public class FixedAsset extends AuditInfo implements WithQuery<FixedAsset>, CommonJpaAudit {

  /**
   * PK, 自增
   */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  /**
   * 资产编号
   */
  @NotNull(message = "资产编号不能为空!", groups = {ValidateGroup.Save.class})
  @Size(message = "资产编号不能超过64个字符", groups = {ValidateGroup.Save.class})
  @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "资产编号只能包含数字/字母/'-'/'_'", groups = {ValidateGroup.Save.class})
  @Column(name = "code_", columnDefinition = "VARCHAR(64)", unique = true, nullable = false)
  private String code;

  /**
   * 资产名称
   */
  @NotNull(message = "资产名称不能为空!", groups = {ValidateGroup.Save.class})
  @Column(name="name_", columnDefinition="VARCHAR(128)")
  private String name;
  /**
   * 增加方式
   * 新购入、二手购入、无票、其它
   */
//  @NotNull(message = "增加方式不能为空!", groups = {ValidateGroup.Save.class})
  @Column(name="add_type", columnDefinition = "VARCHAR(32)")
  private String addType;
  /**
   * 规格型号
   */
  @Column(name="specification", columnDefinition="VARCHAR(128)")
  private String specification;

  /**
   * 生产地
   */
  @Column(name="product_addr", columnDefinition="VARCHAR(128)")
  private String productAddress;
  /**
   * 制造商
   */
  @Column(name="manufacturer", columnDefinition="VARCHAR(128)")
  private String manufacturer;

  /**
   * 供应商
   */
  @Column(name="supplier", columnDefinition="VARCHAR(128)")
  private String supplier;
  /**
   * 存放地点
   */
  @NotNull(message = "存放地点不能为空!", groups = {ValidateGroup.Save.class})
  @Column(name="storage_loc", columnDefinition="VARCHAR(128)")
  private String storageLocation;

  /**
   * 经济用途
   * 生产、管理、其他
   */
  @NotNull(message = "经济用途不能为空!", groups = {ValidateGroup.Save.class})
  @Column(name="usage_type", columnDefinition="VARCHAR(128)")
  private String usageType;
  /**
   * 使用情况
   * 启用/停用
   */
  @Column(name="enabled_", columnDefinition="BIT")
  private boolean enabled = true;

  /**
   * 使用部门，可多个，多个用逗号分隔
   */
//  @NotNull(message = "使用部门不能为空!", groups = {ValidateGroup.Save.class})
  @Column(name="usage_dept", columnDefinition="VARCHAR(1000)")
  private String usageDept;

  @Transient
  private List<Org> depts;

  /**
   * 计量单位
   */
  @Column(name="measurement_unit", columnDefinition="VARCHAR(16)")
  private String measurementUnit;

  /**
   * 单价
   */
//  @NotNull(message = "单价不能为空", groups = {ValidateGroup.Save.class})
  @Positive(message = "单价只能为正数", groups = {ValidateGroup.Save.class})
  @Column(name="unit_price", columnDefinition="DECIMAL(10,2)")
  private BigDecimal unitPrice;

  /**
   * 数量
   */
  @NotNull(message = "数量不能为空", groups = {ValidateGroup.Save.class})
  @Positive(message = "数量只能为正数", groups = {ValidateGroup.Save.class})
  @Column(name="count", columnDefinition="INT")
  private int count;
  /**
   * 发票原值
   * 发票原值=(单价*数量)
   */
  @Column(name="org_inv_val", columnDefinition="DECIMAL(10,2)")
  private BigDecimal originalInvoiceValue;

  /**
   * 入账原值
   */
//  @NotNull(message = "入账原值不能为空", groups = {ValidateGroup.Save.class})
  @Column(name="org_book_val", columnDefinition="DECIMAL(10,2)")
  private BigDecimal originalBookValue;

  /**
   * 采购日期
   */
  @Column(name="buy_date")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate buyDate;
  /**
   * 启用日期
   */
  @Column(name="use_date")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate useDate;

  /**
   * 入账日期
   */
  @Column(name="posting_date", columnDefinition="VARCHAR(128)")
  private LocalDate postingDate;

  /**
   * 预计净残值率%
   * 保存百分后的值
   */
  @Column(name="residual_value_rate", columnDefinition="DECIMAL(6,2)")
  private BigDecimal residualValueRate;
  /**
   * 预计净残值
   */
  @Column(name="residual_value", columnDefinition="DECIMAL(10,2)")
  private BigDecimal residualValue;
  /**
   * 折旧期限
   * 12, 24, 36, 60, 96
   */
  @Column(name="depreciation_period", columnDefinition="TINYINT")
  private int depreciationPeriod;

  /**
   * 折旧到期日
   */
  @Column(name="depreciation_expiry_date")
  @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate depreciationExpiryDate;

  /**
   * 税务科目
   * 科目编号
   */
//  @NotNull(message = "税务科目不能为空", groups = {ValidateGroup.Save.class})
  @Column(name="tax_account_subject", columnDefinition="VARCHAR(16)")
  private String taxAccountSubject;
  /**
   * 核算科目
   */
//  @NotNull(message = "核算科目不能为空", groups = {ValidateGroup.Save.class})
  @Column(name="accounting_subject", columnDefinition="VARCHAR(128)")
  private String accountingSubject;

  /**
   * 月折旧额
   */
  @Column(name="monthly_depreciation_expense", columnDefinition="DECIMAL(10,2)")
  private BigDecimal monthlyDepreciationExpense;
  /**
   * 本年度计提折旧额
   */
  @Column(name="annual_depreciation_expense", columnDefinition="DECIMAL(10,2)")
  private BigDecimal annualDepreciationExpense;

  /**
   * 已计提折旧额
   */
  @Column(name="accrued_depreciation_val", columnDefinition="DECIMAL(10,2)")
  private BigDecimal accruedDepreciationValue;
  /**
   * 累计折旧额
   */
  @Column(name="accumulated_depreciation_val", columnDefinition="DECIMAL(10,2)")
  private BigDecimal accumulatedDepreciationValue;
  /**
   * 固定资产净值
   */
  @Column(name="net_book_val", columnDefinition="DECIMAL(10,2)")
  private BigDecimal netBookValue;
  /**
   * 备注
   */
  @Column(name="remark", columnDefinition="VARCHAR(522)")
  private String remark;

  /**
   * 资产分类
   */
//  @NotNull(message = "资产分类不能为空", groups = {ValidateGroup.Save.class})
  @Column(name="asset_type", columnDefinition="VARCHAR(128)")
  private String assetType;

  @NotNull(message = "归属不能为空", groups = {ValidateGroup.Save.class})
  @Column(name="company_", columnDefinition = "VARCHAR(8)")
  private String company;

  /**
   * 当前月数，不计算当月
   */
  @Column(name="mon_val", columnDefinition = "TINYINT")
  private int monthValue;

  @Transient
  private List<String> usageDeptList;

  @Override
  public FixedAsset afterQuery() {
    return this;
  }


//  @Override
//  public FixedAsset afterQuery() {
//    //计算
//    //本年度计提折旧额=月折旧额*本年度已用月数
//    this.annualDepreciationExpense = this.monthlyDepreciationExpense.multiply(BigDecimal.valueOf(LocalDate.now().getMonthValue()));
//    //累计折旧额=(本年度计提折旧额+已计提折旧额)
//    this.accumulatedDepreciationValue = this.annualDepreciationExpense.add(this.accruedDepreciationValue);
//    //固定资产净值=入账原值-累计折旧额
//    this.netBookValue = originalBookValue.min(this.accumulatedDepreciationValue);
//    return this;
//  }

//  @PrePersist
//  public void  beforePersist() {
//    //计算
//    //发票原值=(单价*数量)
//    this.originalInvoiceValue = this.unitPrice.multiply(BigDecimal.valueOf(this.count));
//    //预计净残值=(入账原值*预计净残值率)
//    this.residualValue = this.originalBookValue.multiply(this.residualValueRate.divide(BigDecimal.valueOf(100))).setScale(2, RoundingMode.HALF_UP);
//    //折旧到期日=（入账日期+折旧期限）
//    this.depreciationExpiryDate = postingDate.plusMonths(this.depreciationPeriod);
//    //月折旧额=（入账原值*（100%-预计净残值率））/折旧期限)
//    this.monthlyDepreciationExpense = this.originalBookValue.multiply(BigDecimal.valueOf(100.00).min(this.residualValueRate).divide(BigDecimal.valueOf(this.depreciationPeriod)));
//  }
}