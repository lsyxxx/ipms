package com.abt.common.model;

import lombok.Data;

import java.util.List;

/**
 * 自动生成表单
 */
@Data
public class AutoForm {
    /**
     * 唯一id
     */
    private String frmId;

    /**
     * 名称
     */
    private String name;

    /**
     * 编号
     */
    private String code;


    private String description;

    /**
     * 表单模板内容Json格式
     */
    private String formJson;

    /**
     * 表单数据
     */
    private String formData;


    /**
     * 可以写入的表单元素id
     */
    private List<String> canWriteFormItemIds;

    private boolean disabled;

    /**
     * 表单类型
     */
    private int frmType;


}
