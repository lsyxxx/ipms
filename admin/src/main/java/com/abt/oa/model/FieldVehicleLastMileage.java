package com.abt.oa.model;

import java.math.BigDecimal;

/** 按实际还车提交顺序得到的单车最近结束里程。 */
public record FieldVehicleLastMileage(String carId, BigDecimal endMileage) {
}
