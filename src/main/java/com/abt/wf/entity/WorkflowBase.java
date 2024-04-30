package com.abt.wf.entity;

import com.abt.common.model.AuditInfo;
import com.abt.wf.config.Constants;
import com.abt.wf.listener.JpaWorkflowListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程基础数据
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(JpaWorkflowListener.class)
public class WorkflowBase extends AuditInfo {

    //-- process
    @Column(name="proc_def_key", columnDefinition="VARCHAR(128)")
    private String processDefinitionKey;

    @Column(name="proc_def_id", columnDefinition="VARCHAR(128)")
    private String processDefinitionId;

    //-- processInstance
    @Column(name="proc_inst_id", columnDefinition="VARCHAR(128)")
    private String processInstanceId;

    /**
     * 业务状态，需要区别流程状态
     * Contants.STATE_DETAIL_*
     */
    @Column(name="biz_state", columnDefinition="VARCHAR(128)")
    private String businessState;

    /**
     * 流程状态，为camunda流程状态
     * ACTIVE, COMPLETED, SUSPEND,...
     */
    @Column(name="proc_state", columnDefinition="VARCHAR(64)")
    private String processState;

    /**
     * 业务是否结束，区别流程
     */
    @Column(name = "is_finished", columnDefinition="BIT")
    private boolean isFinished;

    /**
     * 业务类型，如费用报销，开票确认
     */
    @Column(name="srv_name", columnDefinition="VARCHAR(64)")
    private String serviceName;

    /**
     * 流程结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="end_time")
    private LocalDateTime endTime;

    /**
     * 业务删除原因
     */
    @Column(name="del_reason", columnDefinition="VARCHAR(500)")
    private String deleteReason;

    /**
     * 是否删除业务，只用于手动删除业务, softDelete
     * 审批拒绝不删除业务,
     */
    @Column(name = "is_del", columnDefinition = "BIT")
    private boolean isDelete = false;

    /**
     * 申请人的部门，班组
     */
    @Column(name="create_dept_id", length = 64, columnDefinition = "VARCHAR(64)")
    private String createDeptId;
    @Column(name="create_dept_name", length = 64, columnDefinition = "VARCHAR(64)")
    private String createDeptName;
    @Column(name="create_team_id", length = 64, columnDefinition = "VARCHAR(64)")
    private String createTeamId;
    @Column(name="create_team_name", length = 64, columnDefinition = "VARCHAR(64)")
    private String createTeamName;

    /**
     * 抄送人
     */
    @Column(columnDefinition="VARCHAR(1000)")
    private String copy;

    @Transient
    private String currentTaskId;
    @Transient
    private String currentTaskDefId;
    @Transient
    private String currentTaskName;
    @Transient
    private String currentTaskAssigneeId;
    @Transient
    private String currentTaskAssigneeName;
    @Transient
    private LocalDateTime currentTaskStartTime;

    @Transient
    private String invokedTaskId;
    @Transient
    private String invokedTaskName;
    @Transient
    private String invokedTaskAssigneeId;
    @Transient
    private String invokedTaskAssigneeName;
    @Transient
    private String invokedTaskDefId;
    @Transient
    private boolean isApproveUser;
    @Transient
    private String submitUserid;
    @Transient
    private String submitUsername;


    public List<String> copyList() {
        if (this.getCopy() == null) {
            return List.of();
        }
        return List.of(this.getCopy().split(","));
    }
}
