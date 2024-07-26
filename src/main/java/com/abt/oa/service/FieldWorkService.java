package com.abt.oa.service;

import com.abt.common.model.User;
import com.abt.oa.entity.FieldWork;
import com.abt.oa.entity.FieldWorkAttendanceSetting;
import com.abt.sys.model.entity.EmployeeInfo;

import java.util.List;

public interface FieldWorkService {
    List<FieldWorkAttendanceSetting> findAllSettings();

    void saveSetting(FieldWorkAttendanceSetting fieldAttendanceSetting);

    /**
     * 查询所有可用的补助项目
     */
    List<FieldWorkAttendanceSetting> findAllEnabledAllowance();

    void saveAttendance(FieldWork fwa);

    //查询用户野外考勤审批人
    User findUserReviewer(String userDept);
}
