package com.abt.salary.service;


import com.abt.common.model.R;
import com.abt.salary.entity.SalaryEnc;
import com.abt.salary.entity.SalaryMain;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.model.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

public interface SalaryService {
    SalaryMain createSalaryMain(MultipartFile file, String yearMonth, String group, Integer sheetNo);

    /**
     * 抽取excel数据
     */
    SalaryPreview extractAndValidate(InputStream fis, SalaryMain salaryMain);

    void saveExcelFile(MultipartFile file, SalaryMain main);

    /**
     * 根据工资年月查询
     * @param mid sl_main id
     */
    List<DeptSummary> getDeptSummaryTableByMainId(String mid);

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
    List<UserSalaryDetail> getSalaryDetail(String slipId, String mainId);

    //重置为初始状态，重置密码
    void resetFirst(String jobNumber);

    boolean verifyFirst(String jobNumber);

    /**
     * 修改密码
     */
    void updateEncNotFirst(PwdForm form, String jobNumber);

    void updateEnc(String pwd, String jobNumber);

    void verifyLastPwd(String newPwd, SalaryEnc enc);

    void verifyPwd(String pwd, String jobNumber);

    void verifyConfirmedPwd(String pwd1, String pwd2);

    //根据年月查询UserSlip
    List<UserSlip> findUserSalarySlipByYearMonth(String jobNumber, String yearMonth);

    void readSalarySlip(String slipId);

    //工资条确认
    void checkSalarySlip(String slipId, String checkType);

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

    /**
     * 根据角色查看表格
     * @param checkAuth 用户审核工资权限
     * @param main: salaryMain
     */
    SalaryPreview getSalaryCheckTable(CheckAuth checkAuth, SalaryMain main);

    /**
     * 部门/副总审核
     *
     * @param checkAuth 审核权限
     * @param slids     待审核的slip id list
     */
    R<List<SalarySlip>> deptCheck(CheckAuth checkAuth, List<String> slids);

    List<SalaryMain> findCheckList(String yearMonth, CheckAuth checkAuth);

    String translateCompanyName(String code);

    /**
     * 按工资年月汇总
     * @param yearMonth 工资年月
     */
    List<SalaryMain> summaryCheckList(String yearMonth, CheckAuth checkAuth);

//    List<SlipCount> salaryCountYearMonthByCheckAuth(String yearMonth, CheckAuth checkAuth);

    /**
     * 工资汇总表。ABT分部门，GRD/DC一张表不分部门
     * 1. 不同工资不同表格
     * 2. 按部门合计
     */
    List<SalaryPreview> salarySummaryList(String yearMonth, CheckAuth checkAuth);

    /**
     * 更改用户工资数据
     * 1. 已确认的不能修改
     * 2. 不能修改的数据：姓名
     */
    void updateUserSalaryCell(String cellId, String value);

    /**
     * 发送工资条
     *
     * @param slipId 工资条id
     * @return LocalDateTime 发送时间
     */
    LocalDateTime sendSlipById(String slipId);

    /**
     * 导出审核过的excel(带签名)
     *
     * @param company   完整的公司名称
     * @param yearMonth 工资年月
     * @param checkAuth 权限
     * @param mid       工资main id
     */
    String createCheckExcel(String company, String yearMonth, CheckAuth checkAuth, String mid) throws IOException;

    /**
     * 工资年汇总表，获取当年所有sl_main
     * @param year 年份
     */
    List<SalaryMain> findAllSalaryMainByYearLike(String year);

    void ceoCheckAllByYearMonth(String yearMonth, CheckAuth checkAuth);

    void hrCheckAllByYearMonth(String yearMonth, CheckAuth checkAuth);

    /**
     * 更新实发，用工成本
     */
    @Transactional
    void updateUserSlip(String slipId, SalaryMain main);

    /**
     * 重新计算main中的统计数据
     */
    SalaryMain recalculateSalaryMainSumData(String mid, SalaryMain sm);

    /**
     * 手动个人确认，防止自动确认无效
     * 仅确认未确认的
     */
    void adminUserCheck(String yearMonth);

    List<SalarySlip> findUncheckUserSlipsByMainId(String mainId);

    /**
     * 每个月审核人数合计
     */
    SlipCount ceoSlipCount(String mid);

    SlipCount hrSlipCount(String mid);

    SlipCount dceoSlipCount(String mid, CheckAuth checkAuth);
}
