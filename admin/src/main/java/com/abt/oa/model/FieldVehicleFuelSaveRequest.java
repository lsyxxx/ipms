package com.abt.oa.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

/** 加油表单，日期和说明可空，费用及升数必填。 */
@Getter
@Setter
public class FieldVehicleFuelSaveRequest {
    @NotBlank(message = "用车主记录标识不能为空")
    @Size(max = 50, message = "主记录标识不能超过50个字符")
    private String mainId;
    private LocalDate fuelDate;
    @NotNull(message = "加油费用不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "加油费用必须大于0")
    @Digits(integer = 16, fraction = 2, message = "加油费用最多16位整数和2位小数")
    private BigDecimal amount;
    @NotNull(message = "加油数量不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "加油数量必须大于0")
    @Digits(integer = 15, fraction = 3, message = "加油数量最多15位整数和3位小数")
    private BigDecimal fuelQuantity;
    @Size(max = 1000, message = "加油说明不能超过1000个字符")
    private String remark;
}
