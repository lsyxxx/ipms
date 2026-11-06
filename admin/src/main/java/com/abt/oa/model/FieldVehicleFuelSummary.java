package com.abt.oa.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** 单笔用车的加油聚合投影，COUNT 不排除空日期。 */
public record FieldVehicleFuelSummary(BigDecimal totalQuantity, BigDecimal totalAmount,
                                      Long count, LocalDate latestDate) {
}
