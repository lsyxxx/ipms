package com.abt.salary.service;


import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.model.SalaryPreview;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface SalaryService {
    SalaryMain createSalaryMain(MultipartFile file, String yearMonth, String group, Integer sheetNo);

    /**
     * 抽取excel数据
     */
    SalaryPreview extractAndValidate(InputStream fis, SalaryMain salaryMain);

    void saveExcelFile(MultipartFile file, SalaryMain main);

    //导入数据库和发送工资条到个人
    void salaryImport(SalaryMain main, SalaryPreview preview);

    //根据 工资年月/工资组查看导入/发送工资条记录
    List<SalaryMain> findImportRecordBy(String yearMonth, String group);

    //根据salaryMain.id查询发送情况
    List<SalarySlip> findSendSlips(String mid);

    void deleteSalaryRecord(String mid);

    /**
     * 工资组
     */
    List<String> getSalaryGroup();
}
