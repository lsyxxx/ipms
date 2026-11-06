package com.abt.oa.model;

import com.abt.common.config.ValidateGroup;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/** 新建及使用中修改共同的五项用车资料。 */
@Getter
@Setter
public class FieldVehicleUsageRequest {
    @NotBlank(groups = ValidateGroup.Apply.class, message = "司机不能为空")
    private String drivers;
    private String peers;
    @NotBlank(groups = ValidateGroup.Apply.class, message = "用途不能为空")
    @Size(max = 500, message = "用途不能超过500个字符")
    private String purpose;
    @NotBlank(groups = ValidateGroup.Apply.class, message = "出发地不能为空")
    @Size(max = 200, message = "出发地不能超过200个字符")
    private String departurePlace;
    @NotBlank(groups = ValidateGroup.Apply.class, message = "目的地不能为空")
    @Size(max = 200, message = "目的地不能超过200个字符")
    private String destination;
}
