package com.abt.wf.entity;

import com.abt.common.config.ValidateGroup;
import com.abt.wf.model.act.ActHiProcInstance;
import com.abt.wf.model.act.ActRuTask;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
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
public class Reimburse extends WorkflowBase {

    @Id
    @GeneratedValue(generator  = "timestampIdGenerator")
    @GenericGenerator(name = "timestampIdGenerator", type = com.abt.common.config.TimestampIdGenerator.class)
    private String id;

    @NotNull(groups = {ValidateGroup.Preview.class, ValidateGroup.Save.class})
    @Positive(message = "报销金额不能小于0.00")
    @Column(name="cost", columnDefinition="DECIMAL(10, 2)")
    private Double cost;

    /**
     * 原借款，借了准备金需要填写，没有则不填
     */
    @Positive(message = "原借款金额不能小于0.00")
    @Column(name="reserve_loan", columnDefinition="DECIMAL(10, 2)")
    private Double reserveLoan;

    /**
     * 应退余额，借了准备金有退款余额，没有则不填
     */
    @Positive(message = "应退金额不能小于0.00")
    @Column(name="reserve_refund", columnDefinition="DECIMAL(10, 2)")
    private Double reserveRefund;

    @NotBlank
    @Column(name="reason_", columnDefinition="VARCHAR(500)")
    private String reason;

    /**
     * 报销日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="rbs_date")
    private LocalDate rbsDate;

    @Max(value = 99, message = "票据数量不能超过99")
    @Min(value = 0, message = "票据数量最小为0")
    @Column(name="voucher_num", columnDefinition="TINYINT")
    private int voucherNum;
    /**
     * 报销类型
     */
    @NotNull
    @Column(name="rbs_type", columnDefinition="VARCHAR(256)")
    private String rbsType;

    /**
     * 公司, abt/grd
     */
    @NotNull
    @Column(name="company", columnDefinition="VARCHAR(256)")
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
    @Column(name="team_id", columnDefinition="VARCHAR(128)")
    private String teamId;
    /**
     * 班组/科室名称
     */
    @Column(name = "team_name", columnDefinition="VARCHAR(128)")
    private String teamName;



    /**
     * starter is leader
     */
    @Column(name="is_leader", columnDefinition="BIT")
    private boolean isLeader = false;

    /**
     * 附件信息，json格式保存
     */
    @Column(name = "pdf_file", columnDefinition = "VARCHAR(MAX)")
    private String pdfFileList;

    @Column(name = "other_file", columnDefinition = "VARCHAR(MAX)")
    private String otherFileList;

    /**
     * 选择的审批人 json
     */
    @Column(columnDefinition="VARCHAR(1600)")
    private String managers;



    //-------------------------------------
    //  Transient
    //------------------------------------

//    @OneToOne
//    @JoinColumn(name = "proc_inst_id", referencedColumnName = "ID_", insertable = false, updatable = false)
//    private ActHiProcInstance procInstance;
//
//    @OneToOne
//    @JoinColumn(name = "proc_inst_id", referencedColumnName = "PROC_INST_ID_", insertable = false, updatable = false)
//    private ActRuTask currentTask;

}
