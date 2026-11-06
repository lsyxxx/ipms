package com.abt.oa.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** 物理删除本人草稿的请求，不接收创建人或状态字段。 */
@Getter
@Setter
public class FieldVehicleDeleteRequest {
    @NotBlank(message = "用车记录标识不能为空")
    @Size(max = 50, message = "记录标识不能超过50个字符")
    private String id;
}
