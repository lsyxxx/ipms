package com.abt.safety.entity;

import com.abt.common.config.CommonJpaAuditListener;
import com.abt.common.config.TimestampIdGenerator;
import com.abt.common.config.ValidateGroup;
import com.abt.common.model.AuditInfo;
import com.abt.common.service.CommonJpaAudit;
import com.abt.safety.converter.SafetyFormConverter;
import com.abt.sys.model.WithQuery;
import com.abt.sys.model.entity.SystemFile;
import com.abt.sys.util.SystemFileListConverter;
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
    /**
     * 检查地点
     */
    @Column(name="location_")
    private String location;
    /**
     * 检查人工号
     */
    @Column(name="checker_jno")
    private String checkerJno;
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

    //分配整改负责人
    /**
     * 调度员
     */
    @Column(name="dispatcher_jno")
    private String dispatcherJno;
    
    @Column(name="dispatcher_name")
    private String dispatcherName;

    @Column(name="dispatch_time")
    private LocalDateTime dispatchTime;
   
    /**
     * 负责人/整改人工号
     */
    @Column(name = "rectifier_jno")
    private String rectifierJno;
    @Column(name = "rectifier_name")
    private String rectifierName;

    //整改
    /**
     * 整改说明
     */
    @Column(name = "rectify_remark", length = 1000)
    private String rectifyRemark;
    
    /**
     * 整改提交时间
     */
    @Column(name = "rectify_time")
    private LocalDateTime rectifyTime;

    @Column(name="is_deleted")
    private boolean isDeleted = false;

    /**
     * 流转状态, RecordStatus
     */
    @Column(name="state_", length = 32)
    private String state;

    /**
     * 检查流程是否完成
     */
    @Column(name="is_completed")
    private boolean isCompleted = false;

    /**
     * 上传整改相关文件
     */
    @Convert(converter = SystemFileListConverter.class)
    @Column(name="rectify_files", columnDefinition = "TEXT")
    private List<SystemFile> rectifyFiles;

    @Transient
    private int problemCount;

    @Transient
    private boolean hasProblem = false;

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

    public boolean isRectified() {
        return this.rectifyTime != null;
    }

    public boolean isDispatched() {
        return this.dispatchTime != null;
    }

    public boolean isChecked() {
        return this.checkTime != null;
    }

    @Override
    public SafetyRecord afterQuery() {
        this.calcProblemCount();
        this.calcHasProblem();
        return this;
    }
}
