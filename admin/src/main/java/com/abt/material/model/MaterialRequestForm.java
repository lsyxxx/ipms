package com.abt.material.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Getter
@Setter
public class MaterialRequestForm extends RequestForm {

    /**
     * 物品所属部门
     */
    private String deptId;

    /**
     * 分类id
     */
    private String typeId;

    /**
     * 模糊查询的属性
     */
    private List<String> properties;

    /**
     * 排序属性
     */
    private List<String> orderBy;


    private Boolean isActive;

    
}
