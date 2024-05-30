package com.abt.salary.service;

import com.abt.common.model.ValidationResult;
import com.abt.salary.entity.SalaryDetail;
import com.abt.salary.entity.SalaryMain;

import java.io.InputStream;
import java.util.List;

public interface SalaryService {
    void readSalaryDetail(String fileName, String sheetName);

    SalaryMain createSalaryDetail(String yearMonth, String group, String netPaidColumnName);

    void readSalaryDetail(InputStream inputStream, String sheetName, SalaryMain salaryMain);

    /**
     * 每行数据校验
     * @param salaryDetail 一行明细数据
     */
    ValidationResult salaryDetailRowCheck(SalaryDetail salaryDetail);

    void deleteSalaryAll(String id);

    void saveSalaryDetailBatch(List<SalaryDetail> list);
}
