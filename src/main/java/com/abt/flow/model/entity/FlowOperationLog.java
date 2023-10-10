package com.abt.flow.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程操作日志表
 * T_flow_opt_log
 */
@Schema(description = "流程操作日志表")
@Table(name = "T_flow_opt_log")
@Comment("流程操作日志表")
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class FlowOperationLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Schema(description = "PK")
    private String id;

    @Schema(description = "流程实例id，对应Flowable中的processInstanceId，一个流程实例唯一")
    @Column(name = "procinst_id", columnDefinition = "VARCHAR(128)")
    private String procInstId;

    @Schema(description = "流程定义id，对应Flowable中的procdef_id")
    @Column(name = "procdef_id", columnDefinition = "VARCHAR(128)")
    private String procDefId;

    @Schema(description = "任务id，对应Flowable中的task_id")
    @Column(columnDefinition = "VARCHAR(128)")
    private String taskId;

    @Schema(description = "执行id，对应Flowable中的execution_id")
    @Column(name = "exc_id", columnDefinition = "VARCHAR(128)")
    private String executionId;

    @Schema(description = "活动节点id")
    @Column(name = "act_id", columnDefinition = "VARCHAR(128)")
    private String activityId;

    @Schema(description = "活动节点name")
    @Column(name = "act_name", columnDefinition = "VARCHAR(128)")
    private String activityName;

    @Schema(description = "操作人")
    @Column(name = "opt_user", columnDefinition = "VARCHAR(128)")
    private String operateUser;

    @Schema(description = "操作日期")
    @Column(name = "opt_date", columnDefinition = "DATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateDate;


    @Schema(description = "动作")
    @Column(name = "action", columnDefinition = "VARCHAR(128)")
    private String action;

    @Schema(description = "动作描述")
    @Column(name = "action_des", columnDefinition = "VARCHAR(128)")
    private String actionDescription;


    /**
     * 评论
     */
    @Transient
    private String comment;

    public static FlowOperationLog of() {
        return new FlowOperationLog();
    }



    @Override
    public String toString() {
        return "FlowOperationLog{" +
                "id='" + id + '\'' +
                "procInstId='" + procInstId + '\'' +
                ", procDefId='" + procDefId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", executionId='" + executionId + '\'' +
                ", activityId='" + activityId + '\'' +
                ", activityName='" + activityName + '\'' +
                ", operateUser='" + operateUser + '\'' +
                ", operateDate=" + operateDate +
                ", actionDescription='" + actionDescription + '\'' +
                '}';
    }
}
