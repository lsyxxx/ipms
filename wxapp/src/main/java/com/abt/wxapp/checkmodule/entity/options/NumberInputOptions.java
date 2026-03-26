package com.abt.wxapp.checkmodule.entity.options;

import com.abt.wxapp.checkmodule.model.CheckComponentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * {@code number} 数字输入配置
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("number")
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class NumberInputOptions implements CheckOptions {

    private String placeholder;

    /** 后缀展示，如单位 */
    private String suffix;

    /** 最大值 */
    private BigDecimal max;

    /** 最小值 */
    private BigDecimal min;

    /** 小数精度（小数位数） */
    private Integer precision;
}
