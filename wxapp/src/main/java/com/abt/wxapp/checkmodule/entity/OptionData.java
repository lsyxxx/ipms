package com.abt.wxapp.checkmodule.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 组件值，对应一个CheckComponent
 */
@Getter
@Setter
public class OptionData {

    /**
     * 数据所属的schemeId;
     */
    private String schemeId;

    /**
     * 对应的scheme中的组件的id
     */
    private String checkComponentId;

    /**
     * 数据值
     * 和text主要用来区分checkbox。对应optionItem.value，可以编辑还原。
     * TODO: 统一用string保存，根据checkComponentType再转为对应的数据格式
     */
    private String value;

    /**
     * 数据值文本，对应optionItem.label。
     * 一般用于不可修改的表单的展示
     */
    private String text;


}
