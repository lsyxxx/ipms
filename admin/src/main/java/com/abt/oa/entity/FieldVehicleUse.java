package com.abt.oa.entity;

import com.abt.common.AuditInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 野外用车主记录；可空业务列用于草稿，正式提交由服务校验。 */
@Entity
@Table(name = "oa_field_vehicle_use")
@Getter
@Setter
@DynamicUpdate
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FieldVehicleUse extends AuditInfo {
    /** 用车记录主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 50, nullable = false)
    private String id;
    /** 本次使用的车辆 ID。 */
    @Column(name = "car_id", length = 50)
    private String carId;
    /** 派车时的车牌号快照。 */
    @Column(name = "car_no", columnDefinition = "NVARCHAR(20)")
    private String carNo;
    /** 司机姓名文本，原样保存前端输入。 */
    @Column(name = "drivers", columnDefinition = "NVARCHAR(MAX)")
    private String drivers;
    /** 同行人姓名文本，可空，原样保存前端输入。 */
    @Column(name = "peers", columnDefinition = "NVARCHAR(MAX)")
    private String peers;
    /** 用车用途。 */
    @Column(name = "purpose", columnDefinition = "NVARCHAR(500)")
    private String purpose;
    /** 出发地。 */
    @Column(name = "departure_place", columnDefinition = "NVARCHAR(200)")
    private String departurePlace;
    /** 目的地。 */
    @Column(name = "destination", columnDefinition = "NVARCHAR(200)")
    private String destination;
    /** 实际用车日期。 */
    @Column(name = "use_date")
    private LocalDate useDate;
    /** 预计返回日期，可空。 */
    @Column(name = "expected_return_date")
    private LocalDate expectedReturnDate;
    /** 用车备注。 */
    @Column(name = "remark", columnDefinition = "NVARCHAR(1000)")
    private String remark;
    /** 出车时车辆累计里程，单位：公里。 */
    @Column(name = "start_mileage", precision = 18, scale = 2)
    private BigDecimal startMileage;
    /** 本次用车结束日期。 */
    @Column(name = "end_date")
    private LocalDate endDate;
    /** 还车时车辆累计里程，单位：公里。 */
    @Column(name = "end_mileage", precision = 18, scale = 2)
    private BigDecimal endMileage;
    /** 加油总数量，单位：升；明细变更后重算，结束后冻结。 */
    @Column(name = "fuel_total_quantity", precision = 38, scale = 3, nullable = false)
    private BigDecimal fuelTotalQuantity = new BigDecimal("0.000");
    /** 加油总费用，单位：元；明细变更后重算，结束后冻结。 */
    @Column(name = "fuel_total", precision = 38, scale = 2, nullable = false)
    private BigDecimal fuelTotal = new BigDecimal("0.00");
    /** 加油次数，按加油记录条数统计，结束后冻结。 */
    @Column(name = "fuel_count", nullable = false)
    private Integer fuelCount = 0;
    /** 用车状态：0 草稿、1 使用中、2 已结束。 */
    @Column(name = "status", nullable = false)
    private Integer status;
    /** 办理还车的负责人用户 ID。 */
    @Column(name = "return_user_id", length = 50)
    private String returnUserId;
    /** 办理还车的负责人姓名快照。 */
    @Column(name = "return_username", columnDefinition = "NVARCHAR(255)")
    private String returnUsername;
    /** 还车操作时间，与实际结束日期区分。 */
    @Column(name = "return_time")
    private LocalDateTime returnTime;
}
