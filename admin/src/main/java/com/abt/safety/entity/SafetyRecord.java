package com.abt.safety.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.abt.safety.converter.SafetyFormConverter;
import com.abt.safety.model.CheckType;
import com.abt.safety.model.LocationType;
import com.abt.safety.model.RecordStatus;
import com.abt.sys.model.WithQuery;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 安全检查记录
 * 包含：检查记录，分配负责人，负责人反馈整改情况
 * 若反馈后仍需复查 --> 复查出问题再次整改 多次情况，就加表，仅记录复查/整改过程，关联本表即可。
 */
@Table(name = "safety_record")
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners({CommonJpaAuditListener.class})
public class SafetyRecord extends AuditInfo implements CommonJpaAudit, WithQuery<SafetyRecord> {

    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    //基础信息

    @Enumerated(EnumType.STRING)
    @Column(name="check_type", length = 16)
    private CheckType checkType;

    @Transient
    private String checkTypeDesc;

    @Transient
    private String locationTypeDesc;

    public String getLocationTypeDesc() {
        this.locationTypeDesc = this.locationType == null ? "" : this.locationType.getDescription();
        return this.locationTypeDesc;
    }

    public String getCheckTypeDesc() {
        this.checkTypeDesc =  this.checkType == null ? "" : this.checkType.getDescription();
        return this.checkTypeDesc;
    }

    @Enumerated(EnumType.STRING)
    @Column(name="loc_type", length = 16)
    private LocationType locationType;

    /**
     * 检查地点
     */
    @Column(name="location_")
    private String location;
    /**
     * 检查人工号
     */
    @Column(name="checker_id")
    private String checkerId;
    @Column(name="checker_name")
    private String checkerName;
    /**
     * 检查日期
     */
    @Column(name="check_time")
    private LocalDateTime checkTime;

    /**
     * 表单id
     */
    @Column(name="form_id")
    private Long formId;

    /**
     * 表单实例, json，包含各检查项目，及检查结果
     */
    @Convert(converter = SafetyFormConverter.class)
    @Column(name="check_form_inst", columnDefinition = "TEXT")
    private SafetyForm checkFormInstance;

    @Column(name="is_deleted")
    private boolean isDeleted = false;

    /**
     * 流转状态, RecordStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(name="state_", length = 32)
    private RecordStatus state;

    /**
     * 检查流程是否完成
     */
    @Column(name="is_completed")
    private boolean isCompleted = false;

    @Column(name="complete_time")
    private LocalDateTime completeTime;

    /**
     * 催办时间
     */
    @Column(name="remind_time")
    private LocalDateTime remindTime;

    @Column(name="remind_userid")
    private String remindUserid;

    @Column(name="remind_username")
    private String remindUsername;

    //分配整改负责人
    /**
     * 调度员
     */
    @Column(name="dispatcher_id")
    private String dispatcherId;

    @Column(name="dispatcher_name")
    private String dispatcherName;

    @Column(name="dispatch_time")
    private LocalDateTime dispatchTime;

    /**
     * 负责人/整改人userid
     * 可以看作这个整改的负责人，但是每次单独整改(safetyRectify)可能会改变负责人，但是不影响这里的
     */
    @Column(name = "rectifier_id")
    private String rectifierId;
    @Column(name = "rectifier_name")
    private String rectifierName;

    /**
     * 整改记录列表
     */
    @Transient
    private List<SafetyRectify> safetyRectifyList;

    @Transient
    private int problemCount;

    @Transient
    private boolean hasProblem = false;

    /**
     * 当前正在进行的
     */
    @Transient
    private SafetyRectify currentRectify;

    /**
     * 检查完成/结束
     */
    public void complete() {
        this.isCompleted = true;
        this.completeTime = LocalDateTime.now();
        this.state = RecordStatus.COMPLETED;
    }


    /**
     * 检查问题数量
     */
    public void calcProblemCount() {
        this.problemCount = 0;
        if (this.checkFormInstance != null && this.checkFormInstance.getItems() != null) {
            this.problemCount = (int)this.checkFormInstance.getItems().stream().filter(SafetyFormItem::isProblem).count();
        }
    }

    /**
     * 检查是否存在问题
     */
    public boolean calcHasProblem() {
        this.hasProblem = this.problemCount > 0;
        return this.hasProblem;
    }

    public boolean isChecked() {
        return this.checkTime != null;
    }

    /**
     * 设置催办
     * @param userid 催办人userid
     * @param username 催办人名称
     */
    public void remind(String userid, String username) {
        this.remindUserid = userid;
        this.remindUsername = username;
        this.remindTime = LocalDateTime.now();
    }

    @Override
    public SafetyRecord afterQuery() {
        this.calcProblemCount();
        this.calcHasProblem();
        this.getLocationTypeDesc();
        this.getCheckTypeDesc();
        return this;
    }
}
