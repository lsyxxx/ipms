package com.abt.oa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "fw_item")
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldWorkItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(length = 128, nullable = false)
  private String id;

  @Column(name="f_id", columnDefinition="VARCHAR(128)", nullable = false)
  private String fid;

  @Column(name="a_name", columnDefinition="VARCHAR(128)", nullable = false)
  private String allowanceName;

  @Column(name="a_id", columnDefinition="VARCHAR(128)", nullable = false)
  private String allowanceId;

  @Column(name="a_prod", columnDefinition="DECIMAL(10,2)")
  private double allowanceProdAmount;

  @Column(name="a_meal", columnDefinition="DECIMAL(10,2)")
  private double allowanceMealAmount;

  @Column(name="a_sum", columnDefinition="DECIMAL(10,2)")
  private double sum;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "f_id", referencedColumnName = "id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable=false, updatable=false)
  @NotFound(action= NotFoundAction.IGNORE)
  private FieldWork fieldWork;


  /**
   * 计算合计
   */
  public double sum() {
    this.sum = BigDecimal.valueOf(allowanceProdAmount).add(BigDecimal.valueOf(allowanceMealAmount)).doubleValue();
    return this.sum;
  }

  public static FieldWorkItem create(FieldWorkAttendanceSetting setting, String fid){
    Assert.notNull(fid, "fid must not be null");
    FieldWorkItem item = new FieldWorkItem();
    item.setFid(fid);
    item.setAllowanceId(setting.getId());
    item.setAllowanceName(setting.getName());
    item.setAllowanceProdAmount(setting.getProductionAllowance());
    item.setAllowanceMealAmount(setting.getMealAllowance());
    item.setSum(setting.getSumAllowance());
    return item;
  }

}