package com.abt.sys.repository;

import com.abt.sys.model.entity.EmployeeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeInfo, String> {

    /**
     * 根据工号查询用户
     * @param jobNumber 用户工号
     */
    EmployeeInfo findByJobNumber(String jobNumber);

    List<EmployeeInfo> findByCompanyAndIsActive(String company, String isActive);

}
