package com.abt.salary.model;

import com.abt.salary.entity.SalaryCell;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import lombok.Data;

import java.util.List;

/**
 * 详情
 */
@Data
public class SalaryDetail {

    /**
     * 工资条
     */
    private SalarySlip slip;

    private SalaryMain main;

    private List<SalaryCell> salaryDetails;


}
