package com.abt.salary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 部门工资汇总
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeptSummary {

    private String deptName;
    private Integer sumEmp;
    private BigDecimal sumCost;
    private BigDecimal sumNetPaid;
    private int index;
}
