package com.abt.sys.repository;

import com.abt.sys.model.entity.Employee;

public interface EmployeeRepository {

    /**
     * 根据工号查询用户
     * @param jobNumber 用户工号
     */
    Employee findByJobNumber(String jobNumber);

}
