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


}
