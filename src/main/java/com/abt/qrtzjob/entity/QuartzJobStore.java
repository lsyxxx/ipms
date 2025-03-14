package com.abt.qrtzjob.entity;

import com.abt.qrtzjob.model.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * Quartz job 任务管理
 * 目的是防止服务器重启导致任务丢失，使用该表可以再次创建出任务并继续执行
 */
@Getter
@Setter
@Entity
@Table(name = "qrtz_job_store")
public class QuartzJobStore {

    @Id
    @NotNull
    @Size(max = 512)
    @Column(name="job_id", length = 512, nullable = false)
    private String jobId;

    @NotNull
    @Column(name="name_", nullable = false)
    private String name;

    @NotNull
    @Size(max = 512)
    @Column(name="group_id", length = 512, nullable = false)
    private String groupId;



    @NotNull
    @Column(name="trigger_id", length = 512, nullable = false)
    private String triggerId;

    @Column(name="desc_", length = 1000)
    private String description;

    /**
     * cron表达式
     */
    @Column(name="cron_")
    private String cronExpression;

    /**
     * 调用参数，等于JobDetail中的withJobData中的参数
     */
    @Column(name="job_data")
    private String jobData;

    /**
     * 第一次执行时间, 等于trigger中的startAt
     */
    @Column(name="start_at")
    private LocalDateTime startAt;

    /**
     * 最后一次执行时间
     */
    @Column(name="end_at")
    private LocalDateTime endAt;

    /**
     * 已执行次数
     */
    @Column(name="exec_count")
    private int executeCount;

    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name="status_", nullable = false, length = 32)
    private Status status = Status.PENDING;

    /**
     * job 执行的class名称
     */
    @Column(name="job_clz")
    private String jobClassName;
    

    public int addCount() {
        this.executeCount = this.executeCount + 1;
        return this.executeCount;
    }




}