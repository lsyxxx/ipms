package com.abt.finance.entity;

import com.abt.common.model.AuditInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


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
  @Id
  @Column(name = "id", nullable = false)
  private String id;


}