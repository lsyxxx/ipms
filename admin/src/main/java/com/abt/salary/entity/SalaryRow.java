package com.abt.salary.entity;

import jakarta.persistence.*;

/**
 * 上传工资表的数据
 * 包含excel一行数据
 */
public class SalaryRow {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    //人员信息
    
    @Column(name="emp_num")
    private String jobNumber;
    @Column(name="name_")
    private String name;
    @Column(name="dept_name")
    private String deptName;

    /**
     * excel中的行号
     */
    @Column(name="row_idx")
    private int rowIndex;

    /**
     * 动态列json格式数据
     * TODO: 转为什么格式，需要的信息，列名，数据，（列号）
     */
    @Column(name="dynamic_json", columnDefinition = "VARCHAR(MAX)")
    private String dynamicJson;



}