package com.abt.oa.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class AnnouncementRequestForm extends RequestForm {

    private String fileType;
    private String zdType;


}
