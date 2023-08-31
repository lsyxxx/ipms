package com.abt.flow.model.entity;

import java.time.LocalDate;
import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    @Schema(description = "PK)")
    private String id;

    @Schema(description = "BusinessKey，对应Flowable中的businessKey，唯一")
    @Column(name = "biz_key", columnDefinition = "BusinessKey，对应Flowable中的businessKey，唯一")
    private String businessKey;

    /**
     * 业务类型ID，对应FlowScheme: id
     */
    @Schema(description = "业务类型ID，对应FlowScheme: id")
    @Column(name = "biz_cat_id", columnDefinition = "业务类型ID，对应FlowScheme: id")
    private String bizCategoryId;

    @Schema(description = "业务类型ID，对应FlowScheme: SchemeCode")
    @Column(name = "biz_cat_code", columnDefinition = "业务类型code，对应FlowScheme: SchemeCode")
    private String bizCategoryCode;

    /**
     * 业务类型Name，对应FlowScheme: Name
     */
    @Schema(description = "业务类型Name，对应FlowScheme: SchemeName")
    @Column(columnDefinition = "业务类型Name，对应FlowScheme: SchemeName")
    private String bizCategoryName;
    /**
     * 流程实例id，对应流程引擎中的proc_inst_id
     */
    @Schema(description = "流程实例id，对应流程引擎中的proc_inst_id")
    @Column(name = "proc_id", columnDefinition = "流程实例id，对应流程引擎中的proc_inst_id")
    private String procInstId;
    /**
     * 启动流程用户id，对应User: id
     */
    @Schema(description = "启动流程用户id，对应User: id")
    @Column(columnDefinition = "启动流程用户id，对应User: id")
    private String starterId;
    /**
     * 启动流程用户name, 对应User:Name
     */
    @Schema(description = "启动流程用户name, 对应User:Name")
    @Column(columnDefinition = "启动流程用户name, 对应User:Name")
    private String starterName;
    /**
     * 启动流程时间
     */
    @Schema(description = "启动流程时间")
    @Column(columnDefinition = "启动流程时间")
    private LocalDate startDate;
    /**
     * 流程定义id, 对应流程引擎中act_re_procdef: id
     */
    @Schema(description = "流程定义id, 对应流程引擎中act_re_procdef: id")
    @Column(name = "procdef_id", columnDefinition = "流程定义id, 对应流程引擎中act_re_procdef: id")
    private String procDefId;
    /**
     * 自定义流程状态,
     */
    @Schema(description = "自定义流程状态， 参考ProcessState 类")
    @Column(columnDefinition = "自定义流程状态，参考ProcessState 类")
    private Integer state;
    /**
     * 当前正在进行的流程task，对应act_ru_task: id
     */
    @Schema(description = "当前正在进行的流程task，对应act_ru_task: id")
    @Column(columnDefinition = "当前正在进行的流程task，对应act_ru_task: id")
    private String taskId;
    /**
     * 流程完成时间
     */
    @Schema(description = "流程完成时间")
    @Column(columnDefinition = "流程完成时间")
    private LocalDate endDate;
    /**
     * 流程删除原因
     */
    @Schema(description = "流程删除原因")
    @Column(columnDefinition = "流程删除原因")
    private String delReason;
    /**
     * 流程删除用户
     */
    @Schema(description = "流程删除用户")
    @Column(columnDefinition = "流程删除用户")
    private String delUser;
    /**
     * 流程删除时间
     */
    @Schema(description = "流程删除时间")
    @Column(columnDefinition = "流程删除时间")
    private LocalDate delDate;


    @Schema(description = "最后更新时间")
    @Column(name = "last_update_date", columnDefinition = "最后更新时间")
    @LastModifiedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDate lastUpdateDate;

    @Schema(description = "最后更新用户")
    @Column(columnDefinition = "最后更新用户")
    @LastModifiedBy
    private String lastUpdateUser;
}

