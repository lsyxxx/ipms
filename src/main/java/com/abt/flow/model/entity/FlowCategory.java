package com.abt.flow.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程类型
 */
@Data
@Schema(description = "T_biz_flow_cat流程类型表")
@Table(name = "T_biz_flow_cat")
@Comment("流程类型表")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class FlowCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "PK")
    private String id;

    @Schema(description = "流程类型代码")
    @Column(columnDefinition = "VARCHAR(50)")
    private String code;

    @Schema(description = "流程类型名称")
    @Column(columnDefinition = "VARCHAR(200)")
    private String name;

    @Schema(description = "流程类型描述")
    @Column(columnDefinition = "VARCHAR(255)")
    private String description;

    @Schema(description = "流程分类, eg: 报销,用车,资金流入,资金流出,人事，在设置时录入")
    @Column(columnDefinition = "VARCHAR(255)")
    private String category;

    @Schema(description = "排序代码")
    @Column(columnDefinition = "INT")
    private Integer sortCode = null;

    @Schema(description = "是否删除")
    @Column(name = "is_del", columnDefinition = "SMALLINT")
    private boolean isDeleted = false;

    @Schema(description = "是否启用")
    @Column(name = "is_enable", columnDefinition = "SMALLINT")
    private boolean isEnable = true;

    @Schema(description = "流程定义id")
    @Column(name = "procedef_id", columnDefinition = "VARCHAR(128)")
    private String procDefId;

    @Schema(description = "流程定义文件名称")
    @Column(name = "procdef_file", columnDefinition = "VARCHAR(128)")
    private String procDefFile;

    @Schema(description = "权限类型, 0: 所有人可用, 1: 指定权限")
    @Column(columnDefinition = "INT")
    private Integer authType = 0;

    @Schema(description = "创建用户id")
    @Column(columnDefinition = "VARCHAR(128)")
    @CreatedBy
    private String createUserid;

    @Schema(description = "创建时间")
    @Column(name = "create_date", columnDefinition = "DATE")
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createDate;

    @Schema(description = "创建用户name")
    @Column(columnDefinition = "VARCHAR(128)")
    private String createUsername;

    @Schema(description = "更新用户id")
    @Column(columnDefinition = "VARCHAR(128)")
    @LastModifiedBy
    private String updateUserid;


    @Schema(description = "最后更新时间")
    @Column(name = "update_date", columnDefinition = "DATE")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updateDate;

    @Schema(description = "更新用户name")
    @Column(columnDefinition = "VARCHAR(128)")
    private String updateUsername;



}
