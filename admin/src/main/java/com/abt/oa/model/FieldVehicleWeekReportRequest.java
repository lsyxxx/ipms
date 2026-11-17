package com.abt.oa.model;

import com.abt.common.model.RequestForm;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/** 自然周周报条件，业务日期按上海时区解释。 */
@Getter
@Setter
public class FieldVehicleWeekReportRequest extends RequestForm {
    @NotBlank(message = "周开始日期不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "周开始日期格式必须为yyyy-MM-dd")
    private String weekStart;
    @NotBlank(message = "周结束日期不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "周结束日期格式必须为yyyy-MM-dd")
    private String weekEnd;
    @Size(max = 50, message = "车辆标识不能超过50个字符")
    private String carId;
    @Min(value = 1, message = "周报状态只能为使用中或已结束")
    @Max(value = 2, message = "周报状态只能为使用中或已结束")
    private Integer status;
}
