package com.abt.oa.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Getter
@Setter
public class FieldWorkRequestForm extends RequestForm {
    private String mode;
    private String yearMonth;
    private String company;
    private String dept;

}
