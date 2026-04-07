package com.abt.chkmodule.entity.options;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * {@code input} 单行文本配置
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("input")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextInputOptions implements CheckOptions {

    private String placeholder;

    /** 最大字符数 */
    private Integer max;
}
