package com.abt.oa.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.impl.CommonJpaAudit;
import com.abt.oa.OAConstants;
import com.abt.sys.model.WithQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class FieldWork extends AuditInfo implements CommonJpaAudit, WithQuery<FieldWork> {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    /**
     * 考勤人工号
     */
    @NotNull(groups = {ValidateGroup.Save.class})
    @Column(name="job_number", nullable = false)
    private String jobNumber;

    /**
     * 考勤人userid
     */
    @Column(name="user_id", nullable = false)
    private String userid;

    /**
     * 考勤人姓名
     */
    @NotNull(groups = {ValidateGroup.Save.class})
    @Column(name="user_name", columnDefinition="VARCHAR(32)")
    private String username;

    /**
     * 申请人部门
     */
//    @Column(name="dept_id", columnDefinition="VARCHAR(128)")
//    private String departmentId;
//
//    @Column(name="dept_name", columnDefinition="VARCHAR(128)")
//    private String departmentName;


    @NotNull(groups = {ValidateGroup.Save.class})
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
    @OrderBy("sort ASC")
    private List<FieldWorkItem> items;

    //单个补贴项目id
    @Transient
    private String singleId;

    @Transient
    private List<String> itemIds;

    @Transient
    private List<String> itemNames;


    /**
     * 是否通过
     */
    public boolean isPass() {
        return OAConstants.FW_PASS.equals(this.getReviewResult());
    }

    public boolean isReject() {
        return OAConstants.FW_REJECT.equals(this.getReviewResult());
    }

    /**
     * 是否等待审批
     */
    public boolean isWaiting() {
        return OAConstants.FW_WAITING.equals(this.getReviewResult());
    }

    @Override
    public FieldWork afterQuery() {
        //1.itemIds/itemNames
        if (this.items == null) {
            return this;
        }

        this.itemIds = this.items.stream().map(FieldWorkItem::getId).toList();
        this.itemNames = this.items.stream().map(FieldWorkItem::getAllowanceName).toList();

        return this;
    }

    public void addItem(FieldWorkItem fieldWorkItem) {
        this.items = this.getItems() == null ? new ArrayList<FieldWorkItem>() : this.getItems();
        this.items.add(fieldWorkItem);
    }
}
