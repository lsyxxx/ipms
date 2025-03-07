package com.abt.salary.repository;

import com.abt.salary.entity.SalarySlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalarySlipRepository extends JpaRepository<SalarySlip, String> {

    /**
     * 获取SalarySlip及所有的关联信息EmployeeInfo(department), SalaryMain。
     * 通过对象获取
     * @param mid mainId
     */

    @Query("select s from SalarySlip s " +
            "left join fetch s.salaryMain m " +
            "join fetch s.employeeInfo e " +
            "left join fetch e.department d " +
            "where s.mainId = :mid " +
            "order by s.jobNumber asc")
    List<SalarySlip> findEntireDataByMainIdOrderByJobNumber(String mid);



    @Modifying
    @Query("delete from SalarySlip s where s.mainId = :mainId")
    int deleteAllByMainId(String mainId);

    int countByIsSendAndMainId(boolean isSend, String mainId);
    int countByIsReadAndMainId(boolean isRead, String mainId);
    int countByIsCheckAndMainId(boolean isCheck, String mainId);
    int countByIsFeedBackAndMainId(boolean isFeedBack, String mainId);

    List<SalarySlip> findByJobNumberAndYearMonthContaining(String jobNumber, String yearMonthLike);

    List<SalarySlip> findByJobNumberAndYearMonth(String jobNumber, String yearMonthLike);

    //查询所有用户未确认的(包含未自动确认的)
    @Query("select s from SalarySlip s where (s.isCheck is null or s.isCheck = false) and  s.checkTime is null")
    List<SalarySlip> findAllUnchecked();
}