package com.abt.oa.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.impl.CommonJpaAudit;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 野外工作记录
 * 如果被拒绝，那么此次记录的审批结果是拒绝。
 */
@NamedEntityGraph(name = "all", attributeNodes = {@NamedAttributeNode("items")})
@Table(name = "fw_record", indexes = {
        @Index(name = "idx_user_id", columnList = "create_userid"),
        @Index(name="idx_user_id_date", columnList = "create_userid, atd_date"),
        @Index(name = "idx_atd_date", columnList = "atd_date")
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(CommonJpaAuditListener.class)
public class FieldWork extends AuditInfo implements CommonJpaAudit {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    /**
     * 申请人部门
     */
    @Column(name="dept_id", columnDefinition="VARCHAR(128)")
    private String departmentId;

    @Column(name="dept_name", columnDefinition="VARCHAR(128)")
    private String departmentName;


    @NotNull
    @Column(name="atd_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;

    /**
     * 补助合计
     */
    @Column(name="sum_", columnDefinition="DECIMAL(10,2)")
    private double sum;
    /**
     * 井号
     */
    @Column(name="well", columnDefinition="VARCHAR(128)")
    private String well;
    /**
     * 项目名称
     */
    @Column(name="project", columnDefinition="VARCHAR(500)")
    private String project;

    @Column(name="work_desc", columnDefinition="VARCHAR(500)")
    private String workDescription;

    @NotNull
    @Column(name="rvw_id", columnDefinition="VARCHAR(128)", nullable = false)
    private String reviewerId;

    @Column(name="rvw_name", columnDefinition="VARCHAR(32)")
    private String reviewerName;

    /**
     * 审批部门
     */
    @Column(name="rvw_dept_id", columnDefinition="VARCHAR(128)")
    private String reviewDeptId;

    @Column(name="rvw_dept_name", columnDefinition="VARCHAR(128)")
    private String reviewDeptName;

    /**
     * 审批结果：通过/拒绝
     */
    @Column(name="rvw_result", columnDefinition="VARCHAR(16)")
    private String reviewResult;

    /**
     * 审批时间
     */
    @Column(name="rvw_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;

    /**
     * 审批说明/拒绝原因
     */
    @Column(name="rvw_reason", columnDefinition="VARCHAR(128)")
    private String reviewReason;

    @OneToMany(mappedBy = "fieldWork")
    private List<FieldWorkItem> items;

    @Transient
    private List<String> itemIds;



}
