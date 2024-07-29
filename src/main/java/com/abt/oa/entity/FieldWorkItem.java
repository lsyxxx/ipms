package com.abt.oa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "fw_item")
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

}