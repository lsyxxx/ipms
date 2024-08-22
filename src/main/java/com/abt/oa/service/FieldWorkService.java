package com.abt.oa.service;

import com.abt.common.model.Table;
import com.abt.common.model.User;
import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.model.FieldWorkBoard;
import com.abt.oa.model.FieldWorkRequestForm;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface FieldWorkService {
    List<FieldWorkAttendanceSetting> findAllSettings();

    /**
     * 所有最新的配置，不显示历史记录
     */
    List<FieldWorkAttendanceSetting> findLatestSettings();

    void saveSetting(FieldWorkAttendanceSetting fieldAttendanceSetting);

    /**
     * 查询修改历史记录
     * @param vid 项目公共Id
     */
    List<FieldWorkAttendanceSetting> findHistorySettings(String vid);

    /**
     * 查询所有可用的补助项目
     */
    List<FieldWorkAttendanceSetting> findAllEnabledAllowance();

    /**
     * 查询野外考勤记录
     */
    List<FieldWork> findUserRecord(FieldWork query);

    /**
     * 查询用户野外考勤审批人
     */
    User findUserReviewer(String userDept);

    /**
     * 考勤记录
     */
    void saveFieldWork(FieldWork fw);

    /**
     *
     * @param list 提交的考勤列表数据
     * @param reviewerId 审批人
     * @param reviewerName 审批人姓名
     * @return 错误的考勤数据列表
     */
    List<FieldWork> saveFieldWorkList(List<FieldWork> list, String reviewerId, String reviewerName);

    Page<FieldWork> findTodoRecords(FieldWorkRequestForm form);

    Page<FieldWork> findDoneRecords(FieldWorkRequestForm form);

    Page<FieldWork> findApplyRecords(FieldWorkRequestForm form);

    Page<FieldWork> findAllRecords(FieldWorkRequestForm form);

    /**
     * 查询用户的考勤是数据
     */
    Page<FieldWork> findAtdRecord(FieldWorkRequestForm form);

    void reject(String id, String userid, String reason);

    void pass(String id, String userid);

    //用户看板数据
    FieldWorkBoard userBoard(String jobNumber, String userid, String startDateStr, String endDateStr);

    @Transactional
    void deleteFieldWork(String id, String userid);

    void withdraw(String id, String userid);

    /**
     * 生成考勤统计数据表
     * @param start 开始日期
     * @param end 结束日期
     * @param all 所有野外考勤记录
     */
    Table createStatData(String yearMonth, LocalDate start, LocalDate end, List<FieldWork> all);

    List<FieldWork> findAtdByUserInfo(String jobNumber, String dept, String company, LocalDate start, LocalDate end);

    /**
     * 导出excel
     */
    void writeExcel(Table table);
}
