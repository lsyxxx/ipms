package com.abt.safety.entity;

import com.abt.safety.model.RectifyResult;
import com.abt.sys.model.SystemFile;
import com.abt.sys.util.SystemFileListConverter;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 整改记录
 */
@Table(name = "safety_rectify", indexes = {
        @Index(name = "idx_record_id", columnList = "record_id")
})
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SafetyRectify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * 关联检查记录id
     */
    @Column(name="record_id")
    private String recordId;
    /**
     * 整改上传文件
     */
    @Convert(converter = SystemFileListConverter.class)
    @Column(name="rectify_files", columnDefinition = "TEXT")
    private List<SystemFile> rectifyFiles;

    /**
     * 负责人/整改人userid
     */
    @Column(name = "rectifier_id")
    private String rectifierId;
    @Column(name = "rectifier_name")
    private String rectifierName;

    /**
     * 整改说明
     */
    @Size(max = 1000)
    @Column(name = "rectify_remark", length = 1000)
    private String rectifyRemark;
    /**
     * 整改时间
     */
    @Column(name="rectify_time")
    private LocalDateTime rectifyTime;

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
     * 整改复查评论
     */
    @Size(max = 1000)
    @Column(name="comment", length = 1000)
    private String comment;

    /**
     * 整改复查结果
     */
    @Enumerated(EnumType.STRING)
    @Column(name="check_result", length = 16)
    private RectifyResult checkResult;

    @Column(name="check_time")
    private LocalDateTime checkTime;
    
    @Column(name="checker_id")
    private String checkerId;
    
    @Column(name="checker_name")
    private String checkerName;
    

    /**
     * 整改通过
     */
    public void pass() {
        this.checkResult = RectifyResult.pass;
        this.checkTime = LocalDateTime.now();
    }


    /**
     * 整改不通过
     */
    public void reject() {
        this.checkResult = RectifyResult.reject;
        this.checkTime = LocalDateTime.now();
    }

    /**
     * 整改不通过
     */
    public void reject(String checkerId, String checkerName, String comment) {
        this.checkResult = RectifyResult.reject;
        this.checkTime = LocalDateTime.now();
        this.checkerId = checkerId;
        this.checkerName = checkerName;
        this.comment = comment;
    }

    /**
     * 是否已确认
     */
    public boolean isChecked() {
        return this.checkTime != null && this.checkResult != null;
    }

}
