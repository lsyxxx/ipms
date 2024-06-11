package com.abt.sys.repository;

import com.abt.common.model.User;
import com.abt.sys.model.entity.EmployeeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeInfo, String> {

    /**
     * 根据工号查询用户
     * @param jobNumber 用户工号
     */
    EmployeeInfo findByJobNumber(String jobNumber);

    List<EmployeeInfo> findByCompanyAndIsActive(String company, String isActive);

    List<EmployeeInfo> findByIsExit(boolean isExit);

    /**
     * 查询启动工资的人
     */
    @Query("select e from EmployeeInfo e where e.isActive = '1'")
    List<EmployeeInfo> findAllSalaryEnabled();



}
