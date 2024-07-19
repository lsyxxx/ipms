package com.abt.wf.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter
public class ActivitiRequestForm extends RequestForm {

    private Boolean isFinished;
    private String processInstanceId;
}
