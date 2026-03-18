package com.abt.app.model;

import com.abt.common.model.RequestForm;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Getter
@Setter

public class AppUpdateRequestForm extends RequestForm {

    private String version;
    private String platform;
}
