package com.abt.wf.entity;

import com.abt.common.model.AuditInfo;
import com.abt.common.util.TimeUtil;
import com.abt.wf.model.ReimburseApplyForm;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 报销业务实体
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "wf_rbs")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Reimburse extends AuditInfo {

    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private double cost;

    @Column(name="reason_", columnDefinition="VARCHAR(1000)")
    private String reason;

    /**
     * 报销日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rbsDate;

    @Column(name="voucher_num", columnDefinition="TINYINT")
    private int voucherNum;


    //-- processDefinition
    @Column(name="proc_def_key", columnDefinition="VARCHAR(128)")
    private String processDefinitionKey;
    
    @Column(name="proc_def_id", columnDefinition="VARCHAR(128)")
    private String processDefinitionId;

    //-- processInstance
    @Column(name="proc_inst_id", columnDefinition="VARCHAR(128)")
    private String processInstanceId;
    /**
     * 状态
     * 0：审批中
     * 1. 已通过(整个流程完成)
     * 2. 已拒绝(整个流程完成)
     */
    @Column(name="state_", columnDefinition="TINYINT")
    private int state;

    //-- start
    @Column(name="starter_id", columnDefinition="VARCHAR(128)")
    private String starterId;
    
    @Column(name="starter_name", columnDefinition="VARCHAR(128)")
    private String starterName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    //-- end
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    /**
     * 审批中
     */
    public static final int STATE_APPROVING = 0;
    public static final int STATE_PASS = 1;
    public static final int STATE_REJECT = 2;



    /**
     * 创建一个新的实例
     * @param form 表单
     */
    public static Reimburse create(ReimburseApplyForm form) {
        Reimburse rbs = new Reimburse();
        rbs.setId(TimeUtil.idGenerator());
        rbs.setCost(form.getCost());
        rbs.setReason(form.getReason());
        rbs.setRbsDate(form.getRbsDate());
        rbs.setProcessDefinitionId(form.getProcessDefinitionId());
        rbs.setProcessDefinitionKey(form.getProcessDefinitionKey());
        rbs.setProcessInstanceId(form.getProcessInstanceId());
        rbs.setStarterId(form.getUserid());
        rbs.setStarterName(form.getUsername());
        rbs.setStartTime(LocalDateTime.now());
        rbs.setState(STATE_APPROVING);

        return rbs;
    }

}
