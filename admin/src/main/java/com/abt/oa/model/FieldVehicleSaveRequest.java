package com.abt.oa.model;

import com.abt.common.config.ValidateGroup;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

/** 草稿与正式派车表单，使用 Temp/Apply 分组区分必填。 */
@Getter
@Setter
public class FieldVehicleSaveRequest extends FieldVehicleUsageRequest {
    @Size(max = 50, message = "记录标识不能超过50个字符")
    private String id;
    @NotBlank(groups = ValidateGroup.Apply.class, message = "请选择车辆")
    @Size(max = 50, message = "车辆标识不能超过50个字符")
    private String carId;
    @NotNull(groups = ValidateGroup.Apply.class, message = "用车日期不能为空")
    private LocalDate useDate;
    private LocalDate expectedReturnDate;
    @DecimalMin(value = "0.00", message = "起始公里数不能小于0")
    @Digits(integer = 16, fraction = 2, message = "起始公里数最多16位整数和2位小数")
    private BigDecimal startMileage;
    @Size(max = 1000, message = "备注不能超过1000个字符")
    private String remark;
}
