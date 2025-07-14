package com.abt.testing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 检测清单表
 */
@Getter
@Setter
@Entity
@Table(name = "T_SampleRegist")
public class SampleRegist {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 50)
    @NotNull
    @Column(name = "NewSampleNo", nullable = false, length = 50)
    private String newSampleNo;

    @Size(max = 50)
    @Column(name = "OldSampleNo", length = 50)
    private String oldSampleNo;

    @Column(name = "Xh")
    private Integer xh;

    @Size(max = 1000)
    @Column(name = "Depth", length = 1000)
    private String depth;

    @Size(max = 50)
    @Column(name = "entrustId", length = 50)
    private String entrustId;

    @Size(max = 50)
    @Column(name = "NameDesc", length = 50)
    private String nameDesc;

    @Size(max = 50)
    @Column(name = "CengWei", length = 50)
    private String cengWei;

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
    private LocalDateTime createDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    private LocalDateTime operatedate;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 50)
    @Column(name = "Jname", length = 50)
    private String jname;

    @Size(max = 200)
    @Column(name = "Note", length = 200)
    private String note;

    @Size(max = 8)
    @Column(name = "SampleMaxNo", length = 8)
    private String sampleMaxNo;

    @Size(max = 50)
    @Column(name = "sampleName", length = 50)
    private String sampleName;

    @Size(max = 50)
    @Column(name = "sampleAddress", length = 50)
    private String sampleAddress;

    @Size(max = 50)
    @Column(name = "yali", length = 50)
    private String yali;

    @Size(max = 1)
    @Column(name = "IsZhibei", length = 1)
    private String isZhibei;

    @Size(max = 50)
    @Column(name = "daoyangDate", length = 50)
    private String daoyangDate;

    @Size(max = 150)
    @Column(name = "weituodanwei", length = 150)
    private String weituodanwei;

    @Column(name = "sampledate")
    private LocalDateTime sampledate;

    @Size(max = 200)
    @Column(name = "caiyangren", length = 200)
    private String caiyangren;

}