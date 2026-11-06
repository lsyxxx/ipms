package com.abt.oa.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** 使用中只允许修改五项资料，车辆、日期和里程不在输入白名单内。 */
@Getter
@Setter
public class FieldVehicleUpdateRequest extends FieldVehicleUsageRequest {
    @NotBlank(message = "用车记录标识不能为空")
    @Size(max = 50, message = "记录标识不能超过50个字符")
    private String id;
}
