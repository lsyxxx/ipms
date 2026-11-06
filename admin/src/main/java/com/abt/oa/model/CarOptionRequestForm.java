package com.abt.oa.model;

import com.abt.common.model.RequestForm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/** 车辆列表条件，query 按车牌筛选。 */
@Getter
@Setter
public class CarOptionRequestForm extends RequestForm {
    @Pattern(regexp = "[01]", message = "车辆是否启用只能为0或1")
    @Schema(description = "车辆是否启用：0停用，1启用；不传默认查询启用车辆", defaultValue = "1")
    private String isAvtive = "1";
}
