package com.abt.sys.service;

import com.abt.sys.model.entity.EmployeeInfo;

import java.util.List;

public interface EmployeeService {
    EmployeeInfo findByJobNumber(String jobNumber);

    /**
     * 查询在职/离职员工
     */
    List<EmployeeInfo> findAllByExit(boolean exit);

    List<EmployeeInfo> getByExample(EmployeeInfo condition);

    String getUserCompanyByJobNumber(String jobNumber);
}
