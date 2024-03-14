package com.abt.wfbak.entity;

import com.abt.wfbak.model.ActionEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

/**
 *
 */
@Data
@NoArgsConstructor
@Table(name = "wf_opt_log")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class FlowOperationLog{


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String action;

    /**
     * 操作类型
     * system: 系统操作
     * user: 用户操作
     */
    private String operationType = OPERATE_TYPE_USER;
    public static final String OPERATE_TYPE_SYS = "system";
    public static final String OPERATE_TYPE_USER = "user";

    /**
     * 操作人id
     */
    private String operatorId;
    private String operatorName;
    private LocalDateTime operateTime;
    public static final String OPERATOR_SYS = "system";

    /**
     * 描述
     */
    @Column(name="desc", columnDefinition="VARCHAR(1000)")
    private String description;

    //-- process
    @Column(name="proc_inst_id", columnDefinition="VARCHAR(128)")
    private String processInstanceId;
    @Column(name="proc_def_id", columnDefinition="VARCHAR(128)")
    private String processDefinitionId;
    @Column(name="proc_def_key", columnDefinition="VARCHAR(128)")
    private String processDefinitionKey;

    //-- task
    @Column( columnDefinition="VARCHAR(128)")
    private String taskId;
    /**
     * act_hi_comment id
     */
    @Column(name="comment_id", columnDefinition="VARCHAR(128)")
    private String commentId;


    //TODO
    public static FlowOperationLog create(String operatorId, String operatorName, String processInstanceId, String processDefinitionId, String processDefinitionKey) {
        FlowOperationLog log = new FlowOperationLog();
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setOperateTime(LocalDateTime.now());
        log.setProcessInstanceId(processInstanceId);
        log.setProcessDefinitionId(processDefinitionId);
        log.setProcessDefinitionKey(processDefinitionKey);
        return log;
    }

    /**
     * 用户申请log
     */
    public static FlowOperationLog applyLog(String operatorId, String operatorName, String processInstanceId, String processDefinitionId, String processDefinitionKey, String taskId) {
        FlowOperationLog log = FlowOperationLog.create(operatorId, operatorName, processInstanceId, processDefinitionId, processDefinitionKey);
        log.setTaskId(taskId);
        log.setAction(ActionEnum.APPLY.name());
        log.userOperate();
        return log;
    }

    /**
     * 用户审批通过
     */
    public static FlowOperationLog approvePassLog(String operatorId, String operatorName, String processInstanceId, String processDefinitionId, String processDefinitionKey, String taskId) {
        FlowOperationLog log = FlowOperationLog.create(operatorId, operatorName, processInstanceId, processDefinitionId, processDefinitionKey);
        log.setTaskId(taskId);
        log.setAction(ActionEnum.PASS.name());
        log.userOperate();
        return log;
    }

    /**
     * 用户撤销
     */
    public static FlowOperationLog revokeLog(String operatorId, String operatorName, String processInstanceId, String processDefinitionId, String processDefinitionKey, String taskId) {
        FlowOperationLog log = FlowOperationLog.create(operatorId, operatorName, processInstanceId, processDefinitionId, processDefinitionKey);
        log.setTaskId(taskId);
        log.setAction(ActionEnum.REVOKE.name());
        log.userOperate();
        return log;
    }

    /**
     * 用户手动删除
     */
    public static FlowOperationLog userDeleteLog(String operatorId, String operatorName, String processInstanceId, String processDefinitionId, String processDefinitionKey, String taskId) {
        FlowOperationLog log = FlowOperationLog.create(operatorId, operatorName, processInstanceId, processDefinitionId, processDefinitionKey);
        log.setTaskId(taskId);
        log.setAction(ActionEnum.DELETE.name());
        log.userOperate();
        return log;
    }


    /**
     * 抄送，操作人为系统
     */
    public static FlowOperationLog carbonCopyLog(String processInstanceId, String processDefinitionId, String processDefinitionKey, @Nullable String taskId) {
        FlowOperationLog log = FlowOperationLog.create(OPERATOR_SYS, OPERATOR_SYS, processInstanceId, processDefinitionId, processDefinitionKey);
        log.setTaskId(taskId);
        log.setAction(ActionEnum.COPY.name());
        log.systemOperate();
        return log;
    }

    public void userOperate() {
        this.setOperationType(OPERATE_TYPE_USER);
    }

    public void systemOperate() {
        this.setOperationType(OPERATE_TYPE_SYS);
    }


}
