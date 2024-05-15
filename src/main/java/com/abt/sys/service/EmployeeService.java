package com.abt.sys.service;

import com.abt.sys.model.entity.EmployeeInfo;

public interface EmployeeService {
    EmployeeInfo findByJobNumber(String jobNumber);
}
