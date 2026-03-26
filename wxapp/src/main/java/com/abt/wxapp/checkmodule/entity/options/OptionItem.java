package com.abt.wxapp.checkmodule.entity.options;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * radio / checkbox 选项：{@code id} 为选项值，{@code label} 为展示文案
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OptionItem {

    private String value;

    /** 展示标签 */
    private String label;

    /**
     * 是否禁用该项
     */
    private Boolean disabled;
}
