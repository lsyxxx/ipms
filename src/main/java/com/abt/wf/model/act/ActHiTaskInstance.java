package com.abt.wf.model.act;

import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.entity.WorkflowBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ACT_HI_TASKINST")
public class ActHiTaskInstance {
    @Id
    @Size(max = 64)
    @Nationalized
    @Column(name = "ID_", nullable = false, length = 64)
    private String id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "TASK_DEF_KEY_")
    private String taskDefKey;

    @Size(max = 255)
    @Nationalized
    @Column(name = "PROC_DEF_KEY_")
    private String procDefKey;

    @Size(max = 64)
    @Nationalized
    @Column(name = "PROC_DEF_ID_", length = 64)
    private String procDefId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "ROOT_PROC_INST_ID_", length = 64)
    private String rootProcInstId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "PROC_INST_ID_", length = 64)
    private String procInstId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "EXECUTION_ID_", length = 64)
    private String executionId;

    @Size(max = 255)
    @Nationalized
    @Column(name = "CASE_DEF_KEY_")
    private String caseDefKey;

    @Size(max = 64)
    @Nationalized
    @Column(name = "CASE_DEF_ID_", length = 64)
    private String caseDefId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "CASE_INST_ID_", length = 64)
    private String caseInstId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "CASE_EXECUTION_ID_", length = 64)
    private String caseExecutionId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "ACT_INST_ID_", length = 64)
    private String actInstId;

    @Size(max = 255)
    @Nationalized
    @Column(name = "NAME_")
    private String name;

    @Size(max = 64)
    @Nationalized
    @Column(name = "PARENT_TASK_ID_", length = 64)
    private String parentTaskId;

    @Size(max = 4000)
    @Nationalized
    @Column(name = "DESCRIPTION_", length = 4000)
    private String description;

    @Size(max = 255)
    @Nationalized
    @Column(name = "OWNER_")
    private String owner;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ASSIGNEE_")
    private String assignee;

    @NotNull
    @Column(name = "START_TIME_", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "END_TIME_")
    private LocalDateTime endTime;

    @Column(name = "DURATION_", precision = 19)
    private BigDecimal duration;

    @Size(max = 4000)
    @Nationalized
    @Column(name = "DELETE_REASON_", length = 4000)
    private String deleteReason;

    @Column(name = "PRIORITY_")
    private Integer priority;

    @Column(name = "DUE_DATE_")
    private LocalDateTime dueDate;

    @Column(name = "FOLLOW_UP_DATE_")
    private LocalDateTime followUpDate;

    @Size(max = 64)
    @Nationalized
    @Column(name = "TENANT_ID_", length = 64)
    private String tenantId;

    @Column(name = "REMOVAL_TIME_")
    private LocalDateTime removalTime;


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "proc_inst_id")
//    private InvoiceOffset invoiceOffset;

    @Override
    public String toString() {
        return "ActHiTaskInstance{" +
                "id='" + id + '\'' +
                ", taskDefKey='" + taskDefKey + '\'' +
                ", procDefKey='" + procDefKey + '\'' +
                ", procDefId='" + procDefId + '\'' +
                ", rootProcInstId='" + rootProcInstId + '\'' +
                ", procInstId='" + procInstId + '\'' +
                ", name='" + name + '\'' +
                ", parentTaskId='" + parentTaskId + '\'' +
                ", assignee='" + assignee + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", deleteReason='" + deleteReason + '\'' +
                '}';
    }
}