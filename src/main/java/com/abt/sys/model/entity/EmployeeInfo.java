package com.abt.sys.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "T_EmployeeInfo")
public class EmployeeInfo {
    @Id
    @Size(max = 50)
    @Column(name = "Id", nullable = false, length = 50)
    private String id;

    @Size(max = 255)
    @NotNull
    @Column(name = "JobNumber", nullable = false)
    private String jobNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "Name", nullable = false)
    private String name;

    @Size(max = 255)
    @NotNull
    @Column(name = "Dept", nullable = false)
    private String dept;

    @Size(max = 30)
    @Column(name = "Position", length = 30)
    private String position;

    @Size(max = 10)
    @Column(name = "Sex", length = 10)
    private String sex;

    @Size(max = 30)
    @Column(name = "Nation", length = 30)
    private String nation;

    @Size(max = 30)
    @Column(name = "Edu", length = 30)
    private String edu;

    @Size(max = 30)
    @Column(name = "PartyMember", length = 30)
    private String partyMember;

    @Size(max = 30)
    @Column(name = "AncestralPlace", length = 30)
    private String ancestralPlace;

    @Size(max = 50)
    @Column(name = "Mobile", length = 50)
    private String mobile;

    @Size(max = 500)
    @Column(name = "GraduateSchool", length = 500)
    private String graduateSchool;

    @Size(max = 500)
    @Column(name = "Major", length = 500)
    private String major;

    @Column(name = "Birthday")
    private LocalDateTime birthday;

    @Column(name = "Age")
    private Integer age;

    @Size(max = 50)
    @Column(name = "IDCARD", length = 50)
    private String idcard;

    @Column(name = "IDExpriyDate")
    private LocalDateTime iDExpriyDate;

    @Column(name = "RZDay")
    private LocalDateTime rZDay;

    @Size(max = 50)
    @Column(name = "RZNX", length = 50)
    private String rznx;

    @Size(max = 50)
    @Column(name = "SYQ", length = 50)
    private String syq;

    @Column(name = "ZZDate")
    private LocalDateTime zZDate;

    @Size(max = 500)
    @Column(name = "ContractType", length = 500)
    private String contractType;

    @Column(name = "ConQDDate")
    private LocalDateTime conQDDate;

    @Column(name = "ConExpriyDate")
    private LocalDateTime conExpriyDate;

    @Size(max = 20)
    @Column(name = "ConNX", length = 20)
    private String conNX;

    @Size(max = 50)
    @Column(name = "EmergencyContact", length = 50)
    private String emergencyContact;

    @Size(max = 50)
    @Column(name = "EmergencyTel", length = 50)
    private String emergencyTel;

    @Size(max = 200)
    @Column(name = "HomeAddress", length = 200)
    private String homeAddress;

    @Size(max = 200)
    @Column(name = "RegisteredResidence", length = 200)
    private String registeredResidence;

    @Size(max = 200)
    @Column(name = "RegisteredXZ", length = 200)
    private String registeredXZ;

    @Size(max = 50)
    @Column(name = "ZC", length = 50)
    private String zc;

    @Size(max = 150)
    @Column(name = "ZYZSY", length = 150)
    private String zyzsy;

    @Column(name = "ZSYExpriyDate")
    private LocalDateTime zSYExpriyDate;

    @Size(max = 150)
    @Column(name = "ZYZSE", length = 150)
    private String zyzse;

    @Column(name = "ZSEExpriyDate")
    private LocalDateTime zSEExpriyDate;

    @Size(max = 150)
    @Column(name = "ZYZSSAN", length = 150)
    private String zyzssan;

    @Column(name = "ZYZSSANExpriyDate")
    private LocalDateTime zYZSSANExpriyDate;

    @Size(max = 150)
    @Column(name = "ZYZSSI", length = 150)
    private String zyzssi;

    @Column(name = "ZYZSSIExpriyDate")
    private LocalDateTime zYZSSIExpriyDate;

    @Size(max = 150)
    @Column(name = "ZYZSW", length = 150)
    private String zyzsw;

    @Column(name = "ZYZSWExpriyDate")
    private LocalDateTime zYZSWExpriyDate;

    @Size(max = 150)
    @Column(name = "ZYZSL", length = 150)
    private String zyzsl;

    @Column(name = "ZYZSLExpriyDate")
    private LocalDateTime zYZSLExpriyDate;

    @Size(max = 500)
    @Column(name = "NOTE", length = 500)
    private String note;

    @NotNull
    @Column(name = "CreateDate", nullable = false)
    private LocalDateTime createDate;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserId", nullable = false, length = 50)
    private String createUserId;

    @Size(max = 50)
    @NotNull
    @Column(name = "CreateUserName", nullable = false, length = 50)
    private String createUserName;

    @Size(max = 50)
    @NotNull
    @Column(name = "Operator", nullable = false, length = 50)
    private String operator;

    @Size(max = 50)
    @NotNull
    @Column(name = "OperatorName", nullable = false, length = 50)
    private String operatorName;

    @NotNull
    @Column(name = "Operatedate", nullable = false)
    private LocalDateTime operatedate;

    @Size(max = 4)
    @Column(name = "IsActive", length = 4)
    private String isActive;

    @Lob
    @Column(name = "thpapers")
    private String thpapers;

    @Lob
    @Column(name = "papers")
    private String papers;

    @Lob
    @Column(name = "zpImage")
    private String zpImage;

    @Lob
    @Column(name = "bypapers")
    private String bypapers;

    @Lob
    @Column(name = "edupapers")
    private String edupapers;

    @Lob
    @Column(name = "idcardzhengmian")
    private String idcardZhengmian;

    @Lob
    @Column(name = "idcardfanmianpapers")
    private String idcardFanmianpapers;

    @Size(max = 50)
    @Column(name = "banzhudept", length = 50)
    private String banzhuDept;

    @Size(max = 20)
    @Column(name = "postxingzhi", length = 20)
    private String postXingzhi;

    @Size(max = 20)
    @Column(name = "guanlifangshi", length = 20)
    private String guanlifangshi;

    @Column(name = "biYedate")
    private LocalDateTime biYeDate;

    @Column(name = "LzDate")
    private LocalDateTime lzDate;

    @Column(name = "IsExit")
    private boolean isExit;

    @Column(name = "ContractFile")
    private String contractFile;

    @Column(name="Company", length = 32)
    private String company;

}