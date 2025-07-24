package com.abt.testing.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @Column(name = "ReportDate")
    private LocalDateTime reportDate;

    @Size(max = 2)
    @Column(name = "IsSamply", length = 2)
    private String isSamply;

    @Column(name = "JianCePrice", precision = 18, scale = 2)
    private BigDecimal jianCePrice;

    @Size(max = 20)
    @Column(name = "FkType", length = 20)
    private String fkType;

    @Size(max = 1000)
    @Column(name = "EntrustDesc", length = 1000)
    private String entrustDesc;

    @Size(max = 1000)
    @Column(name = "Note", length = 1000)
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CreateDate")
    private LocalDateTime createDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "Operatedate")
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
    private String reportfilepath;

    @Size(max = 16)
    @Column(name = "company_", length = 16)
    private String company;

    @Size(max = 20)
    @Column(name = "ywfl", length = 20)
    private String ywfl;

    @Size(max = 20)
    @Column(name = "ywgs", length = 20)
    private String ywgs;

    @Size(max = 20)
    @Column(name = "ccyq", length = 20)
    private String ccyq;

    @Size(max = 300)
    @Column(name = "ccyqqt", length = 300)
    private String ccyqqt;

    @Size(max = 20)
    @Column(name = "lysj", length = 20)
    private String lysj;

    @Size(max = 20)
    @Column(name = "isfb", length = 20)
    private String isfb;

    @Size(max = 300)
    @Column(name = "fbproject", length = 300)
    private String fbproject;

    @Size(max = 20)
    @Column(name = "jlpd", length = 20)
    private String jlpd;

    @Size(max = 20)
    @Column(name = "jylb", length = 20)
    private String jylb;

    @Size(max = 20)
    @Column(name = "jylbqt", length = 20)
    private String jylbqt;

    @Size(max = 20)
    @Column(name = "yycl", length = 20)
    private String yycl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "songyangdate")
    private LocalDateTime songyangdate;

    @Transient
    private String songyangDateStr;

    private String getSongyangDateStr(DateTimeFormatter formatter) {
        return songyangdate == null ? "" :  songyangdate.format(formatter);
    }

}