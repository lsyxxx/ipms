package com.abt.wf.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 *
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "wf_rbs")
@DynamicInsert
@DynamicUpdate
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Reimburse extends WorkflowBase {

    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    @DecimalMin(value = "0.00", message = "报销金额不能小于0.00")
    private Double cost;

    @Column(name="reason_", columnDefinition="VARCHAR(500)")
    private String reason;

    /**
     * 报销日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate rbsDate;

    @Max(value = 99, message = "票据数量不能超过99")
    @Min(value = 0, message = "票据数量最小为0")
    @Column(name="voucher_num", columnDefinition="TINYINT")
    private int voucherNum;
    /**
     * 报销类型
     */
    @Column(name="rbs_type", columnDefinition="VARCHAR(256)")
    private String rbsType;

    /**
     * 公司, abt/grd
     */
    @Column( columnDefinition="VARCHAR(256)")
    private String company;
    /**
     * 关联项目
     */
    @Column(columnDefinition="VARCHAR(1000)")
    private String project;

    /**
     * 部门
     */
    @Column(name="dept_id", columnDefinition="VARCHAR(128)")
    private String departmentId;
    @Column(name="dept_name", columnDefinition="VARCHAR(128)")
    private String departmentName;

    /**
     * 班组/科室Id
     */
    @Column(columnDefinition="VARCHAR(128)")
    private String teamId;
    /**
     * 班组/科室名称
     */
    @Column(columnDefinition="VARCHAR(128)")
    private String teamName;



    /**
     * starter is leader
     */
    @Column(columnDefinition="BIT")
    private boolean isLeader = false;

    /**
     * 附件信息，json格式保存
     */
    @Column(columnDefinition="VARCHAR(1000)")
    private String fileList;

    /**
     * 选择的审批人 json
     */
    @Column(columnDefinition="VARCHAR(1000)")
    private String managerList;



}
