package com.abt.wxapp.checkmodule.entity.options;

import com.abt.wxapp.checkmodule.model.CheckComponentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * {@code radio} 配置
 */
@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("radio")
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RadioOptions implements CheckOptions {

    private List<OptionItem> items;
}
