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

    @Query("select new com.abt.sys.model.entity.EmployeeInfo(e, u.id) from EmployeeInfo e left join TUser u on u.empnum = e.jobNumber where e.isExit = :isExit")
    List<EmployeeInfo> findByIsExit(boolean isExit);

    /**
     * 查询启动工资的人
     */
    @Query("select e from EmployeeInfo e where e.isActive = '1'")
    List<EmployeeInfo> findAllSalaryEnabled();

    @Query("select new com.abt.sys.model.entity.EmployeeInfo(e, u.id) from EmployeeInfo e left join TUser u on u.empnum = e.jobNumber where u.id = :userid")
    List<EmployeeInfo> findAllByUserid(String userid);

    @Query("select distinct e.company from EmployeeInfo e")
    List<String> findDistinctGroup();

    @Query("SELECT e FROM EmployeeInfo e JOIN FETCH e.department where e.jobNumber = :jobNumber order by e.jobNumber")
    List<EmployeeInfo> findWithDeptByJobNumber(String jobNumber);

    @Query("SELECT e FROM EmployeeInfo e JOIN FETCH e.department order by e.jobNumber")
    List<EmployeeInfo> findAllWithDept();
}
