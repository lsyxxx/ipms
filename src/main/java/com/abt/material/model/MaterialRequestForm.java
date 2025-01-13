package com.abt.material.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
}
