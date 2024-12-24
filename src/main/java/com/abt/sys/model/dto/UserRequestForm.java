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
public class UserRequestForm extends RequestForm {
    private String jobNumber;
    private int status;

}
