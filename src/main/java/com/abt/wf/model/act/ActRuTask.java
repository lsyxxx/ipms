package com.abt.wf.model.act;

import com.abt.common.model.User;
import com.abt.sys.model.dto.UserView;
import com.abt.sys.model.entity.EmployeeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ACT_RU_TASK")
@ToString
public class ActRuTask {
    @Id
    @Size(max = 64)
    @Nationalized
    @Column(name = "ID_", nullable = false, length = 64)
    private String id;

    @Column(name = "REV_")
    private Integer rev;

    @Column(name = "EXECUTION_ID_")
    private String executionId;

    @Column(name = "PROC_INST_ID_")
    private String procInstId;

    @Column(name = "PROC_DEF_ID_")
    private String procDefId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "CASE_INST_ID_", length = 64)
    private String caseInstId;

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
    @Column(name = "TASK_DEF_KEY_")
    private String taskDefKey;

    @Size(max = 255)
    @Nationalized
    @Column(name = "OWNER_")
    private String owner;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ASSIGNEE_")
    private String assignee;

    @Size(max = 64)
    @Nationalized
    @Column(name = "DELEGATION_", length = 64)
    private String delegation;

    @Column(name = "PRIORITY_")
    private Integer priority;

    @Column(name = "CREATE_TIME_")
    private LocalDateTime createTime;

    @Column(name = "LAST_UPDATED_")
    private LocalDateTime lastUpdated;

    @Column(name = "DUE_DATE_")
    private LocalDateTime dueDate;

    @Column(name = "FOLLOW_UP_DATE_")
    private LocalDateTime followUpDate;

    @Column(name = "SUSPENSION_STATE_")
    private Integer suspensionState;

    @Size(max = 64)
    @Nationalized
    @Column(name = "TENANT_ID_", length = 64)
    private String tenantId;

    @OneToOne
    @JoinColumn(name = "ASSIGNEE_", referencedColumnName = "Id", insertable = false, updatable = false)
    private EmployeeInfo assigneeInfo;


}