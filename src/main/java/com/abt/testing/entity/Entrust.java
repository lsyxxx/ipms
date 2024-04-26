package com.abt.testing.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "T_entrust")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entrust {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 50)
    @Column(name = "HtBianHao", length = 50)
    private String htBianHao;

    @Size(max = 50)
    @Column(name = "CustomNo", length = 50)
    private String customNo;

    @Size(max = 50)
    @Column(name = "SendSample", length = 50)
    private String sendSample;

    @Size(max = 50)
    @Column(name = "ReceiveEmp", length = 50)
    private String receiveEmp;

    @Size(max = 50)
    @Column(name = "SaleEmp", length = 50)
    private String saleEmp;

    @Size(max = 2)
    @Column(name = "IsJiaji", length = 2)
    private String isJiaji;

    @Size(max = 20)
    @Column(name = "ReportType", length = 20)
    private String reportType;

    @Size(max = 20)
    @Column(name = "SendReportType", length = 20)
    private String sendReportType;

    @Column(name = "ReportDate", columnDefinition = "DATETIME")
    private Instant reportDate;

    @Size(max = 2)
    @Column(name = "IsSamply", length = 2)
    private String isSamply;

    @Column(name = "JianCePrice", precision = 18, scale = 2)
    private BigDecimal jianCePrice;

    @Size(max = 20)
    @Column(name = "FkType", length = 20)
    private String fkType;

    @Size(max = 300)
    @Column(name = "EntrustDesc", length = 300)
    private String entrustDesc;

    @Size(max = 300)
    @Column(name = "Note", length = 300)
    private String note;

    @Lob
    @Column(name = "FilePath")
    private String filePath;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @NotNull
    @Column(name = "CreateDate", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime createDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @NotNull
    @Column(name = "Operatedate", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime operatedate;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @Size(max = 50)
    @Column(name = "ypRegister", length = 50)
    private String ypRegister;

    @Size(max = 50)
    @Column(name = "isweituoPrint", length = 50)
    private String isweituoPrint;

    @Size(max = 2)
    @Column(name = "status", length = 2)
    private String status;

    @Size(max = 50)
    @Column(name = "deptUser", length = 50)
    private String deptUser;

    @Size(max = 300)
    @Column(name = "ProjectName", length = 300)
    private String projectName;

    @Column(name = "SampleNum")
    private Integer sampleNum;

    @Size(max = 300)
    @Column(name = "JiaFangCompany", length = 300)
    private String jiaFangCompany;

    @Size(max = 50)
    @Column(name = "reporttelphone", length = 50)
    private String reporttelphone;

    @Size(max = 300)
    @Column(name = "reportAddres", length = 300)
    private String reportAddres;

    @Column(name = "ReportNum")
    private Integer reportNum;

    @Size(max = 20)
    @Column(name = "sendSampleTel", length = 20)
    private String sendSampleTel;

    @Size(max = 2000)
    @Column(name = "reportfilepath", length = 2000)
    private String reportFilePath;

    @Override
    public String toString() {
        return "Entrust{" +
                "id='" + id + '\'' +
                ", htBianHao='" + htBianHao + '\'' +
                ", customNo='" + customNo + '\'' +
                '}';
    }
}