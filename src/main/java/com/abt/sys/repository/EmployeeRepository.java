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

    @Query("select new com.abt.sys.model.entity.EmployeeInfo(u.id, e.id, e.name, e.dept) from EmployeeInfo e left join TUser u on u.empnum = e.jobNumber where e.isExit = :isExit")
    List<EmployeeInfo> findByIsExit(boolean isExit);

//    @Query("select e from EmployeeInfo e where e.isActive = '1'")
//    List<EmployeeInfo> findAllSalaryEnabled();
//
//    @Query("select new com.abt.sys.model.entity.EmployeeInfo(e, u.id) from EmployeeInfo e left join TUser u on u.empnum = e.jobNumber where u.id = :userid")
//    List<EmployeeInfo> findAllByUserid(String userid);

    @Query("select distinct e.company from EmployeeInfo e")
    List<String> findDistinctGroup();

//    @Query("SELECT e FROM EmployeeInfo e JOIN FETCH e.department where e.jobNumber = :jobNumber order by e.jobNumber")
//    List<EmployeeInfo> findWithDeptByJobNumber(String jobNumber);

    @Query("SELECT e FROM EmployeeInfo e LEFT JOIN FETCH e.department order by e.jobNumber ")
    List<EmployeeInfo> findAllWithDept();

    @Query("select e from EmployeeInfo e left join fetch e.department d " +
            "left join e.tUser u " +
            "where d.status = 1 " +
            "and u.status = 1 " +
            "order by d.cascadeId, u.empnum asc")
    List<EmployeeInfo> findAllEnabledDeptUsers();


    @Query("select e from EmployeeInfo e " +
            "inner join fetch TUser u on e.jobNumber = u.empnum " +
            "where u.id = :userid")
    EmployeeInfo findOneByUserid(String userid);




}
