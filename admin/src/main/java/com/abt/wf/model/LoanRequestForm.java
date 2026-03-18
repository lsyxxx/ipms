package com.abt.wf.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LoanRequestForm extends RequestForm {

    private String project;
    private String deptId;
    private String payType;
}
