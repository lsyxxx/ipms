package com.abt.finance.entity;

import com.abt.common.model.AuditInfo;
import com.abt.sys.model.entity.Org;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
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
public class FixedAsset extends AuditInfo {

  /**
   * 流水号: 资产分类-
   */
  @Id
  @Column(name = "id", nullable = false, unique = true, updatable = false)
  private String id;

  /**
   * 资产名称
   */
  @Column(name="name_", columnDefinition="VARCHAR(128)")
  private String name;
  /**
   * 增加方式
   * 新购入、二手购入、无票、其它
   */
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
  @Column(name="storage_loc", columnDefinition="VARCHAR(128)")
  private String storageLocation;

  /**
   * 经济用途
   * 生产、管理、其他
   */
  @Column(name="usage_type", columnDefinition="VARCHAR(128)")
  private String usageType;
  /**
   * 使用情况
   * 启用/停用
   */
  @Column(name="enabled_", columnDefinition="BIT")
  private String enabled;

  /**
   * 使用部门，可多个，多个用逗号分隔
   */
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
  @Column(name="unit_price", columnDefinition="DECIMAL(10,2)")
  private BigDecimal unitPrice;

  /**
   * 数量
   */
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
  private String postingDate;

  /**
   * 预计净残值率%
   * TODO: 保存百分后的值
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
  @Column(name="tax_account_subject", columnDefinition="VARCHAR(16)")
  private String taxAccountSubject;
  /**
   * 核算科目
   */
  @Column(name="accounting_subject", columnDefinition="VARCHAR(128)")
  private String accountingSubject;

  /**
   * 月折旧额
   */
  @Column(name="monthly_depreciation_expense", columnDefinition="DECIMAL(10,2)")
  private String monthlyDepreciationExpense;
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
   * 房屋建筑物，实验设备，器具工具家具，运输工具，电子办公设备
   */
  @Column(name="asset_type", columnDefinition="VARCHAR(128)")
  private String assetType;

  @Column(name="company_", columnDefinition = "VARCHAR(8)")
  private String company;



}