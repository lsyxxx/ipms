package com.abt.oa.service;

import com.abt.common.model.User;
import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.oa.model.FieldWorkRequestForm;
import com.abt.oa.model.FieldWorkUserBoard;
import com.abt.sys.model.entity.EmployeeInfo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FieldWorkService {
    List<FieldWorkAttendanceSetting> findAllSettings();

    void saveSetting(FieldWorkAttendanceSetting fieldAttendanceSetting);

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

    Page<FieldWork> findTodoRecords(FieldWorkRequestForm form);

    Page<FieldWork> findDoneRecords(FieldWorkRequestForm form);

    Page<FieldWork> findApplyRecords(FieldWorkRequestForm form);

    Page<FieldWork> findAllRecords(FieldWorkRequestForm form);

    void reject(String id, String userid, String reason);

    void pass(String id, String userid);

    //用户看板数据
    FieldWorkUserBoard userBoard(String jobNumber, String userid, String startDateStr, String endDateStr);
}
