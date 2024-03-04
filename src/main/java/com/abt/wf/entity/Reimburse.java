package com.abt.wf.entity;

import com.abt.common.model.AuditInfo;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.TimeUtil;
import com.abt.wf.model.ReimburseApplyForm;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
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

    @Column(name="reason_", columnDefinition="VARCHAR(500)")
    private String reason;

    /**
     * 报销日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate rbsDate;

    @Column(name="voucher_num", columnDefinition="TINYINT")
    private int voucherNum;


    /**
     * 报销类型
     */
    @Column(name="rbs_type", columnDefinition="VARCHAR(64)")
    private String rbsType;


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
     * 99. 暂存
     */
    @Column(name="state_", columnDefinition="TINYINT")
    private int state;

    //-- start
    @Column(name="starter_id", columnDefinition="VARCHAR(128)")
    private String starterId;
    
    @Column(name="starter_name", columnDefinition="VARCHAR(128)")
    private String starterName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    //-- end
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 附件信息，json格式保存
     */
    @Column(columnDefinition="VARCHAR(1000)")
    private String fileList;

    /**
     * 审批中
     */
    public static final int STATE_APPROVING = 0;
    /**
     * 审批通过
     */
    public static final int STATE_PASS = 1;
    /**
     * 审批拒绝
     */
    public static final int STATE_REJECT = 2;
    /**
     * 撤销
     */
    public static final int STATE_CANCEL = 4;

    /**
     * 暂存
     */
    public static final int STATE_TEMP = 99;



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
        rbs.setVoucherNum(form.getVoucherNum());
        rbs.setProcessDefinitionId(form.getProcessDefinitionId());
        rbs.setProcessDefinitionKey(form.getProcessDefinitionKey());
        rbs.setProcessInstanceId(form.getProcessInstanceId());
        rbs.setStarterId(form.getUserid());
        rbs.setStarterName(form.getUsername());
        rbs.setStartTime(LocalDateTime.now());
        rbs.setState(STATE_APPROVING);
        rbs.setRbsType(form.getRbsType());
        if (!CollectionUtils.isEmpty(form.getAttachments())) {
            rbs.setFileList(JsonUtil.convertJson(form.getAttachments()));
        }

        return rbs;
    }

    public static Reimburse createTemp(ReimburseApplyForm form) {
        Reimburse rbs = create(form);
        rbs.setState(Reimburse.STATE_TEMP);
        return rbs;
    }

}
