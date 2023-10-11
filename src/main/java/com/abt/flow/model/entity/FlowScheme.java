package com.abt.flow.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Comment;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * OpenAuth FlowScheme表
 */
@Data
@Accessors(chain = true)
@Schema(description = "自定义页面")
public class FlowScheme {


    private String id;

    private String schemeCode;

    private String schemeName;

    private String schemeType;

    private String schemeVersion;

    private String schemeCanUser;
    /**
     * 表单json
     */
    private String schemeContent;
    private String frmId;
    private int frmType;
    private int authorizeType;
    private int sortCode;
    private boolean deleteMark = false;
    private boolean disabled = false;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private String createUserId;
    private String createUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDate;
    private String modifyUserId;
    private String modifyUserName;

    private String schemeName1;

    /**
     * Flowable流程定义ID
     */
    private String processDefId;

    private String orgId;


}
