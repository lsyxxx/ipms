package com.abt.safety.model;

import com.abt.common.model.RequestForm;
import com.abt.safety.entity.SafetyForm;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class SafetyFormRequestForm extends RequestForm {
    private Boolean formEnabled;
    private String location;
    private String responsible;
}
