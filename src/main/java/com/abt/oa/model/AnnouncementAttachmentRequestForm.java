package com.abt.oa.model;

import com.abt.common.model.RequestForm;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncementAttachmentRequestForm extends RequestForm {
    private String isHf;
    private String isSer;
    private String fileType;
}
