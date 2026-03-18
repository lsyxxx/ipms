package com.abt.salary.entity;

import com.abt.common.listener.JpaListStringConverter;
import com.abt.common.model.User;
import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.wf.entity.UserSignature;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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