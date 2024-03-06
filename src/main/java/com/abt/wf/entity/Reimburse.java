package com.abt.wf.entity;

import com.abt.common.model.AuditInfo;
import com.abt.common.util.JsonUtil;
import com.abt.common.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
    private String id;

    @DecimalMin(value = "0.00", message = "报销金额不能小于0.00")
    private double cost;

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

    //-- processDefinition
    @Column(name="proc_def_key", columnDefinition="VARCHAR(128)")
    private String processDefinitionKey;
    
    @Column(name="proc_def_id", columnDefinition="VARCHAR(128)")
    private String processDefinitionId;

    //-- processInstance
    @Column(name="proc_inst_id", columnDefinition="VARCHAR(128)")
    private String processInstanceId;
    /**
     * 流程状态
     * 参考：STATE_
     */
    @Column(name="state_", columnDefinition="TINYINT")
    private int state;

    //-- starter
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

    @Size(max = 500, message = "流程删除原因最多输入500字")
    @Column(columnDefinition="VARCHAR(1000)")
    private String deleteReason;

    @Column(columnDefinition="BIT")
    private boolean isFinished = false;

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

    /**
     * 审批中
     */
    public static final int STATE_APPROVING = 0;
    /**
     * 审批通过(流程已完成)
     */
    public static final int STATE_PASS = 1;
    /**
     * 审批拒绝(流程已完成)
     */
    public static final int STATE_REJECT = 2;
    /**
     * 撤销
     */
    public static final int STATE_CANCEL = 3;

    /**
     * 暂存
     */
    public static final int STATE_TEMP = 99;

    /**
     * 驳回流程
     */
    public void reject() {
        this.state = STATE_REJECT;
        this.endTime = LocalDateTime.now();
        this.isFinished = true;
    }

}
