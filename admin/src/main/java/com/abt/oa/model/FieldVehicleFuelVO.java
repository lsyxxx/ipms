package com.abt.oa.model;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

/** 加油明细及其独立创建、修改审计。 */
@Getter
@Setter
public class FieldVehicleFuelVO extends FieldVehicleAuditVO {
    private String id;
    private String mainId;
    private LocalDate fuelDate;
    private BigDecimal amount;
    private BigDecimal fuelQuantity;
    private String remark;
}
