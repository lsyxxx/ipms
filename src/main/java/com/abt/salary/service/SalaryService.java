package com.abt.salary.service;


import com.abt.common.model.ValidationResult;
import com.abt.salary.entity.SalaryCell;
import com.abt.salary.entity.SalaryEnc;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.model.SalaryPreview;
import com.abt.salary.model.UserSlip;
import jakarta.servlet.http.HttpSession;
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
    List<SalaryMain> findImportRecordBy(String yearMonth);

    SalaryMain findSalaryMainById(String id);

    //根据salaryMain.id查询发送情况
    List<SalarySlip> findSendSlips(String mid);

    void deleteSalaryRecord(String mid);

    /**
     * 工资组
     */
    List<String> getSalaryGroup();

    //获取当年所有工资条
    List<UserSlip> findUserSlipListByCurrentYear(String jobNumber);

    void verifySessionTimeout(HttpSession session);

    /**
     * 根据slip id查询一条工资条详情
     */
    List<SalaryCell> getSalaryDetail(String slipId, String mainId);

    //重置为初始状态
    void resetFirst();

    boolean verifyFirst(String jobNumber);

    void updateEnc(String pwd, String jobNumber);

    void verifyPwd(String pwd, String jobNumber);

    void verifyConfirmedPwd(String pwd1, String pwd2);

    //根据年月查询UserSlip
    List<UserSlip> findUserSalarySlipByYearMonth(String jobNumber, String yearMonth);

    void readSalarySlip(String slipId);

    //工资条确认
    void checkSalarySlip(String slipId);

    /**
     *  自动确认
     */
    void slipAutoCheck(String slipId);

    /**
     * 查询所有未确认的（手动未确认和自动未确认）
     */
    List<SalarySlip> findSalarySlipUnchecked();

    void getSlipAutoCheckTime(SalarySlip slip);

    void saveAllSlip(List<SalarySlip> list);
}
