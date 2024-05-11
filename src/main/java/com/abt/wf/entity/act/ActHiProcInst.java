package com.abt.wf.entity.act;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ACT_HI_PROCINST")
public class ActHiProcInst {
    @Id
    @Size(max = 64)
    @Nationalized
    @Column(name = "ID_", nullable = false, length = 64, columnDefinition = "NVARCHAR")
    private String id;

    @Size(max = 64)
    @NotNull
    @Nationalized
    @Column(name = "PROC_INST_ID_", nullable = false, length = 64, columnDefinition = "NVARCHAR")
    private String procInstId;

    @Size(max = 255)
    @Nationalized
    @Column(name = "BUSINESS_KEY_", columnDefinition = "NVARCHAR")
    private String businessKey;

    @Size(max = 255)
    @Nationalized
    @Column(name = "PROC_DEF_KEY_", columnDefinition = "NVARCHAR")
    private String procDefKey;

    @Size(max = 64)
    @NotNull
    @Nationalized
    @Column(name = "PROC_DEF_ID_", nullable = false, length = 64, columnDefinition = "NVARCHAR")
    private String procDefId;

    @NotNull
    @Column(name = "START_TIME_", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "END_TIME_")
    private LocalDateTime endTime;

    @Column(name = "REMOVAL_TIME_")
    private LocalDateTime removalTime;

    @Column(name = "DURATION_", precision = 19)
    private Integer duration;

    @Size(max = 255)
    @Nationalized
    @Column(name = "START_USER_ID_", columnDefinition = "NVARCHAR")
    private String startUserId;

    @Size(max = 255)
    @Nationalized
    @Column(name = "START_ACT_ID_", columnDefinition = "NVARCHAR")
    private String startActId;

    @Size(max = 255)
    @Nationalized
    @Column(name = "END_ACT_ID_", columnDefinition = "NVARCHAR")
    private String endActId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "SUPER_PROCESS_INSTANCE_ID_", length = 64, columnDefinition = "NVARCHAR")
    private String superProcessInstanceId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "ROOT_PROC_INST_ID_", length = 64, columnDefinition = "NVARCHAR")
    private String rootProcInstId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "SUPER_CASE_INSTANCE_ID_", length = 64, columnDefinition = "NVARCHAR")
    private String superCaseInstanceId;

    @Size(max = 64)
    @Nationalized
    @Column(name = "CASE_INST_ID_", length = 64, columnDefinition = "NVARCHAR")
    private String caseInstId;

    @Size(max = 4000)
    @Nationalized
    @Column(name = "DELETE_REASON_", length = 4000, columnDefinition = "NVARCHAR")
    private String deleteReason;

    @Size(max = 64)
    @Nationalized
    @Column(name = "TENANT_ID_", length = 64, columnDefinition = "NVARCHAR")
    private String tenantId;

    @Size(max = 255)
    @Nationalized
    @Column(name = "STATE_", columnDefinition = "NVARCHAR")
    private String state;

}