package com.abt.flow.model.entity;

import java.time.LocalDate;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * (BizFlowRelation)实体类
 *
 * @author makejava
 * @since 2023-08-30 14:24:28
 */
@Data
@Schema(description = "业务-流程关系表")
@Table(name = "T_biz_flow_rel")
@Comment("业务-流程关系表")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class BizFlowRelation implements Serializable {
    private static final long serialVersionUID = -93782996630936457L;
    /**
     * PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "PK")
    private String id;

    @Schema(description = "BusinessKey,对应Flowable中的businessKey,唯一")
    @Column(name = "biz_key", columnDefinition = "VARCHAR(128)")
    private String businessKey;

    /**
     * 业务类型ID,对应FlowSchema: id
     */
    @Schema(description = "业务类型ID,对应FlowSchema: id")
    @Column(name = "biz_cat_id", columnDefinition = "VARCHAR(128)", nullable = false)
    private String bizCategoryId;

    @Schema(description = "业务类型ID,对应FlowSchema: SchemeCode")
    @Column(name = "biz_cat_code", columnDefinition = "VARCHAR(128)", nullable = false)
    private String bizCategoryCode;

    /**
     * 业务类型Name,对应FlowSchema: Name
     */
    @Schema(description = "业务类型Name,对应FlowSchema: SchemeName")
    @Column(columnDefinition = "VARCHAR(128)")
    private String bizCategoryName;
    /**
     * 流程实例id,对应流程引擎中的proc_inst_id
     */
    @Schema(description = "流程实例id,对应流程引擎中的proc_inst_id")
    @Column(name = "proc_id", columnDefinition = "VARCHAR(128)")
    private String procInstId;
    /**
     * 启动流程用户id,对应User: id
     */
    @Schema(description = "启动流程用户id,对应User: id")
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String starterId;
    /**
     * 启动流程用户name, 对应User:Name
     */
    @Schema(description = "启动流程用户name, 对应User:Name")
    @Column(columnDefinition = "VARCHAR(255)")
    private String starterName;
    /**
     * 启动流程时间
     */
    @Schema(description = "启动流程时间")
    @Column(columnDefinition = "Date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    /**
     * 流程定义id, 对应流程引擎中act_re_procdef: id
     */
    @Schema(description = "流程定义id, 对应流程引擎中act_re_procdef: id")
    @Column(name = "procdef_id", columnDefinition = "VARCHAR(128)")
    private String procDefId;
    /**
     * 自定义流程状态,
     */
    @Schema(description = "自定义流程状态, 参考ProcessState 类")
    @Column(columnDefinition = "VARCHAR(128)")
    private Integer state;
    /**
     * 当前正在进行的流程task,对应act_ru_task: id
     */
    @Schema(description = "当前正在进行的流程task,对应act_ru_task: id")
    @Column(columnDefinition = "VARCHAR(128)")
    private String taskId;
    /**
     * 流程完成时间
     */
    @Schema(description = "流程完成时间")
    @Column(columnDefinition = "Date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    /**
     * 流程删除原因
     */
    @Schema(description = "流程删除原因")
    @Column(columnDefinition = "TEXT")
    private String delReason;
    /**
     * 流程删除用户
     */
    @Schema(description = "流程删除用户")
    @Column(columnDefinition = "VARCHAR(255)")
    private String delUser;
    /**
     * 流程删除时间
     */
    @Schema(description = "流程删除时间")
    @Column(columnDefinition = "DATE")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate delDate;


    @Schema(description = "最后更新时间")
    @Column(name = "last_update_date", columnDefinition = "DATE")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastUpdateDate;

    @Schema(description = "最后更新用户")
    @Column(columnDefinition = "VARCHAR(255)")
    @LastModifiedBy
    private String lastUpdateUser;
}

