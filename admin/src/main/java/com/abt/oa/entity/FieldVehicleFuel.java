package com.abt.oa.entity;

import com.abt.common.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;

/** 野外用车加油明细，沿用通用创建及修改审计。 */
@Entity
@Table(name = "oa_field_vehicle_fuel", indexes = @Index(name = "IX_ofvf_main_date", columnList = "m_id,fuel_date,id"))
@Getter
@Setter
@DynamicUpdate
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FieldVehicleFuel extends AuditInfo {
    /** 加油记录主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;
    /** 所属野外用车主记录 ID，对应数据库列 m_id。 */
    @Column(name = "m_id", length = 50, nullable = false)
    private String mainId;
    /** 实际加油日期，可空。 */
    @Column(name = "fuel_date")
    private LocalDate fuelDate;
    /** 本次加油费用，单位：元。 */
    @Column(name = "amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal amount;
    /** 本次加油数量，单位：升。 */
    @Column(name = "fuel_quantity", precision = 18, scale = 3, nullable = false)
    private BigDecimal fuelQuantity;
    /** 加油说明或备注，可空。 */
    @Column(name = "remark", columnDefinition = "NVARCHAR(1000)")
    private String remark;
}
