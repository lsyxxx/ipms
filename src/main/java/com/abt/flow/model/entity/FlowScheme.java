package com.abt.flow.model.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * OpenAuth FlowScheme表
 */
@Data
@Accessors(chain = true)
public class FlowScheme {

    private String schemeCode;
    private String schemeName;
    private String schemeType;
    private String schemeVersion;
    private String schemeCanUser;
    private String schemeContent;
    private String frmId;
    private int frmType;
    private int authorizeType;
    private int sortCode;
    private int deleteMark;
    private int disabled;
    private String description;
    private LocalDateTime createDate;
    private String createUserId;
    private String createUserName;
    private LocalDateTime modifyDate;
    private String modifyUserId;
    private String modifyUserName;
    private String schemeName1;

    /**
     * Flowable流程定义ID
     */
    private String processDefId;


}
