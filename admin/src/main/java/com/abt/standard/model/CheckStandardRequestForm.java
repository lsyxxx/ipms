package com.abt.standard.model;


import com.abt.chkmodule.model.StandardStatus;
import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckStandardRequestForm extends RequestForm {


    /**
     * 标准状态
     */
    private StandardStatus status;

    /**
     * 标准等级
     */
    private List<String> levels;
}