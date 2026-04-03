package com.abt.chkmodule.entity.options;

import com.abt.common.config.ValidateGroup;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * radio / checkbox 选项：{@code value} 为选项值，{@code label} 为展示文案
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OptionItem {

    @NotBlank(message = "OptionItem必须输入value", groups = {ValidateGroup.Save.class})
    private String value;

    /** 展示标签 */
    @NotBlank(message = "OptionItem必须输入label", groups = {ValidateGroup.Save.class})
    private String label;

    /** 是否禁用该项 */
    private boolean disabled = false;
}
