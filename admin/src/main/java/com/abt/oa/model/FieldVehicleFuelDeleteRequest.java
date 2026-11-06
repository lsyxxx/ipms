package com.abt.oa.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/** 删除加油记录的主从归属校验参数。 */
@Getter
@Setter
public class FieldVehicleFuelDeleteRequest {
    @NotBlank(message = "加油记录标识不能为空")
    @Size(max = 50, message = "加油记录标识不能超过50个字符")
    private String id;
    @NotBlank(message = "用车主记录标识不能为空")
    @Size(max = 50, message = "主记录标识不能超过50个字符")
    private String mainId;
}
