package com.abt.sys.model.dto;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class EmployeeInfoRequestForm extends RequestForm {

    /**
     * 是否离职
     */
    private Boolean isExit;

    private String username;

    private String jobNumber;

    private String deptId;

    /**
     * 是否有签名
     */
    private Boolean hasSig;

    private String company;

}
