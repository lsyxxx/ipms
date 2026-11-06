package com.abt.oa.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 用车列表，读取主表持久化汇总，加油变更时重算，结束后冻结。 */
@Getter
@Setter
public class FieldVehicleListVO extends FieldVehicleAuditVO {
    private String id;
    private String carId;
    private String carNo;
    private String drivers;
    private String peers;
    private String purpose;
    private String departurePlace;
    private String destination;
    private LocalDate useDate;
    private LocalDate expectedReturnDate;
    private String remark;
    private BigDecimal startMileage;
    private LocalDate endDate;
    private BigDecimal endMileage;
    private BigDecimal usedMileage;
    private BigDecimal fuelTotalQuantity;
    private BigDecimal fuelTotal;
    private Integer fuelCount;
    private Integer status;
    private String statusName;
    private String returnUserId;
    private String returnUsername;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime returnTime;
}
