package com.abt.oa.model;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

/** 车辆使用状态与起始表显公里数提示。 */
public record FieldVehicleCarOptionVO(String carId, String carNo, BigDecimal startMileage,
                                      boolean requiresInitialMileage,
                                      @Schema(description = "是否正在使用：0空闲，1使用中") String isUse) {
}
