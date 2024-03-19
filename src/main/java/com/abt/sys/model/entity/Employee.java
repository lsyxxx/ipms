package com.abt.sys.model.entity;

import lombok.Data;

/**
 *
 */ 
@Data
public class Employee {
    private String id;
    private String jobNumber;
    private String name;
    /**
     * 部门
     */
    private String deptI;
    private String deptName;
    /**
     * 班组
     */
    private String subDeptId;
    private String subDeptName;
    /**
     * 岗位;
     */
    private String position;

    /**
     * 性别
     * 1:男
     * 2:女
     */
    private int sex;

    /**
     * 民族
     */
    private String nation;
    /**
     * 学历
     */
    private String education;


//     Dept
//     ZYZSSANExpriyDate
//     EmergencyTel
//     Name
//     GraduateSchool
//     Major
//     SYQ
//     ZYZSL
//     AncestralPlace
//     ConNX
//     ContractType
//     Age   0
//     ConQDDate
//     CreateUserId
//     CreateUserName
//     CreateDate
//     Operator
//     OperatorName
//     Operatedate
//     RegisteredXZ
//     ZC
//     Mobile
//     IDExpriyDate
//     ZZDate
//     Position
//     ConExpriyDate
//     Sex
//     RegisteredResidence
//     ZYZSE
//     Edu
//     EmergencyContact
//     Nation
//     HomeAddress
//     IDCARD
//     NOTE
//     ZSYExpriyDate
//     JobNumber
//     ZYZSLExpriyDate
//     RZNX
//     ZYZSSIExpriyDate
//     ZYZSY
//     RZDay
//     PartyMember
//     ZYZSSAN
//     ZSEExpriyDate
//     ZYZSW
//     Birthday
//     ZYZSWExpriyDate
//     ZYZSSI
//     IsActive
//     thpapers
//     papers
//     zpImage
//     bypapers
//     edupapers
//     idcardzhengmian
//     idcardfanmianpapers
//     banzhudept
//     postxingzhi
//     guanlifangshi
//     biYedate
//     lzDate
}
