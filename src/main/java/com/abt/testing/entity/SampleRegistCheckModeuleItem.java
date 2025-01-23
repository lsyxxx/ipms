package com.abt.testing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.time.LocalDateTime;


/**
 * 样品-检测项目关联表
 */
@Getter
@Setter
@Entity
@Table(name = "T_SampleRegist_CheckModeuleItem")
public class SampleRegistCheckModeuleItem {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 50)
    @Column(name = "SampleRegistId", length = 50)
    private String sampleRegistId;

    @Size(max = 50)
    @Column(name = "entrustId", length = 50)
    private String entrustId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CheckModeuleId", nullable = false, length = 50)
    private String checkModeuleId;

    @Size(max = 100)
    @Nationalized
    @Column(name = "CheckModeuleName", length = 100)
    private String checkModeuleName;

    @Size(max = 50)
    @Column(name = "ExprotName", length = 50)
    private String exprotName;

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

    @Lob
    @Column(name = "FilePath")
    private String filePath;

    @Size(max = 1)
    @Column(name = "ischeckd", length = 1)
    private String ischeckd;

    @Column(name = "checkdate")
    private LocalDateTime checkdate;

}