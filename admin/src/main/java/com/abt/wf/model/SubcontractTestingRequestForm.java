package com.abt.wf.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 */
@Getter
@Setter
public class SubcontractTestingRequestForm extends RequestForm {
    private String id;
    private String subcontractCompanyName;

    private List<String> ids;
}
