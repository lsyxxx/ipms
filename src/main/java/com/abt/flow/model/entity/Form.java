package com.abt.flow.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 表单模板
 */

@Schema(description = "表单")
@Data
public class Form {

    private String id;

    private String name;

    /**
     * 表单类型，0：默认动态表单；1：Web自定义表单
     */
    private int frmType;
    /**
     * 系统页面标识，当表单类型为用Web自定义的表单时，需要标识加载哪个页面
     */
    private String webId;

    /**
     * 字段个数
     */
    private int fields;

    /**
     * 表单中的控件属性描述
     */
    private String contentData;

    /**
     * 表单控件位置模板
     */
    private String contentParse;

    /**
     * 表单原html模板未经处理的
     */
    private String content;
    private int sortCode;
    private boolean deleteMark = false;
    private String dbName;
    private boolean disabled = false;

    /**
     * 备注
     */
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @CreatedBy
    private String createUserId;
    private String createUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifyDate;

    @LastModifiedBy
    private String modifyUserId;
    private String modifyUserName;

    /**
     * 所属部门
     */
    private String orgId;

}
