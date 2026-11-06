package com.abt.oa.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/** 修改加油明细，必须同时校验所属主记录。 */
@Getter
@Setter
public class FieldVehicleFuelUpdateRequest extends FieldVehicleFuelSaveRequest {
    @NotBlank(message = "加油记录标识不能为空")
    @Size(max = 50, message = "加油记录标识不能超过50个字符")
    private String id;
}
