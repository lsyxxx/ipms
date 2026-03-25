package com.abt.wxapp.checkmodule.entity.options;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * {@code checkbox} 配置
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("checkbox")
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CheckboxOptions implements CheckOptions {

    private List<OptionItem> items;
}
