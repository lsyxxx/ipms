package com.abt.wf.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class SubcontractTestingRequestForm extends RequestForm {
    private String id;
    private String subcontractCompanyName;
}
