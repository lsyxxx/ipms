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

//    /**
//     * 全部在职员工
//     */
//    @Query("select new com.abt.sys.model.entity.EmployeeInfo(e.id, tu.id,  e.name, e.jobNumber, e.dept, e.createUserId, e.createUserName, e.createDate, " +
//            "e.operator, e.operatorName, e.operatedate) " +
//            "from EmployeeInfo e join TUser tu on e.jobNumber = tu.empnum " +
//            "where e.isExit = :isExit " +
//            "order by e.jobNumber")
//    List<EmployeeInfo> findUserByIsExit(boolean isExit);


}
