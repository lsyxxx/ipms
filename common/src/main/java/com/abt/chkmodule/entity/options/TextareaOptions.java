package com.abt.chkmodule.entity.options;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * {@code textarea} 富文本/多行文本配置
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("textarea")
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TextareaOptions extends TextInputOptions {

}
