package com.abt.testing.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "T_checkModule")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TCheckmodule {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "Fid", nullable = false, length = 50)
    private String fid;

    @Size(max = 50)
    @NotNull
    @Column(name = "Fname", nullable = false, length = 50)
    private String fname;

    @Size(max = 50)
    @Column(name = "CheckunitId", length = 50)
    private String checkunitId;

    @Size(max = 500)
    @Column(name = "Note", length = 500)
    private String note;

    @Size(max = 2)
    @Column(name = "IsActive", length = 2)
    private String isActive;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @NotNull
    @Column(name = "CreateDate", nullable = false)
    private Instant createDate;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    private Instant operatedate;

    @Size(max = 2)
    @Column(name = "delFlag", length = 2)
    private String delFlag;

    @Size(max = 50)
    @Column(name = "BreName", length = 50)
    private String breName;

    @Size(max = 50)
    @Column(name = "Isdiag", length = 50)
    private String isdiag;

    @Size(max = 50)
    @Column(name = "IsPic", length = 50)
    private String isPic;

    @Size(max = 2)
    @Column(name = "IsCanshu", length = 2)
    private String isCanshu;

    @Size(max = 2)
    @Column(name = "IsUpload", length = 2)
    private String isUpload;

    @Size(max = 2)
    @Column(name = "IsPicReport", length = 2)
    private String isPicReport;

    @Size(max = 1)
    @Column(name = "isTeshuProject", length = 1)
    private String isTeshuProject;

    @Size(max = 2)
    @Column(name = "shifourenzheng", length = 2)
    private String shifourenzheng;

    @Size(max = 20)
    @Column(name = "ReportMuban", length = 20)
    private String reportMuban;

    @Size(max = 50)
    @Column(name = "yangpinguige", length = 50)
    private String yangpinguige;


}