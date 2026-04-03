package com.abt.chkmodule.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 组件值，对应一个 {@link CheckComponent}
 */
@Getter
@Setter
public class OptionData {

    /** 数据所属的 schemeId */
    private String schemeId;

    /** 对应的 scheme 中的组件 id */
    private String checkComponentId;

    /**
     * 数据值；与 text 主要用来区分 checkbox。对应 optionItem.value。
     */
    private String value;

    /** 数据值文本，对应 optionItem.label；一般用于不可修改的表单展示 */
    private String text;
}
