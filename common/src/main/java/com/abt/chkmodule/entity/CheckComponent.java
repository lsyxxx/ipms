package com.abt.chkmodule.entity;

import com.abt.chkmodule.entity.options.CheckOptions;
import com.abt.chkmodule.model.CheckComponentType;
import com.abt.chkmodule.model.PayType;
import com.abt.common.config.ValidateGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 检测项目组件。{@link #type} 与 {@link #options} 中 JSON 的 {@code kind} 应对应同一类控件（如 input + kind=input）。
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckComponent {

    @NotBlank(message = "动态表单必须添加组件必须输入id", groups = {ValidateGroup.Save.class})
    private String id;

    /** 组件名称/标题。一般用于前端的标题展示 */
    @NotBlank(message = "动态表单必须添加组件必须输入组件标题名称(label)", groups = {ValidateGroup.Save.class})
    private String label;

    /** 组件类型 */
    @NotNull(groups = {ValidateGroup.Save.class}, message = "动态表单必须输入组件类型(type)")
    private CheckComponentType type;

    /**
     * 类型专属配置；序列化为 JSON 时带 {@code kind} 字段，与 {@link #type} 语义一致
     */
    @Valid
    private CheckOptions options;

    /** 用户提示 */
    private String tips;

    /** 是否必填 */
    private boolean required = false;

    /**
     * 本付款方式
     * 默认无费用
     */
    private PayType payType = PayType.free;

    /** 单价 */
    private BigDecimal price;

    /** 组件顺序，必须 */
    private int sortNo;

    public CheckComponent() {
        this.id = UUID.randomUUID().toString();
    }
}
