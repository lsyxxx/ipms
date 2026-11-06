package com.abt.oa.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

/** 还车输入，合计完全由后端重算，不接收客户端汇总。 */
@Getter
@Setter
public class FieldVehicleReturnRequest {
    @NotBlank(message = "用车记录标识不能为空")
    @Size(max = 50, message = "记录标识不能超过50个字符")
    private String id;
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    @NotNull(message = "结束公里数不能为空")
    @DecimalMin(value = "0.00", message = "结束公里数不能小于0")
    @Digits(integer = 16, fraction = 2, message = "结束公里数最多16位整数和2位小数")
    private BigDecimal endMileage;
}
