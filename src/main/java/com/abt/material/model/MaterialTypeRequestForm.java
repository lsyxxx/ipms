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
public class MaterialTypeRequestForm extends RequestForm {
    /**
     * ids字符串，多个用逗号分隔
     */
    private List<String> ids;

    private Boolean isDeleted;

}
