package com.abt.salary.repository;

import com.abt.salary.entity.SalarySlip;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface SalarySlipRepository extends JpaRepository<SalarySlip, String> {

    /**
     * 获取SalarySlip及所有的关联信息EmployeeInfo(department), SalaryMain。
     * 通过对象获取
     * @param mid mainId
     */
    @Query("select s from SalarySlip s " +
            "left join fetch s.salaryMain m " +
            "left join fetch s.employeeInfo e " +
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

    //TODO: 会单独查询user,employeeInfo表，个别u_sig单独查询，没找到原因
    @Query("""
          select s from SalarySlip s 
          left join UserSignature u on s.jobNumber = u.jobNumber and s.isCheck = true 
          where s.mainId = :mainId 
          and s.jobNumber in :jmSet 
""")
    List<SalarySlip> findWithSignatureByMainIdAndJobNumberSet(String mainId, Set<String> jmSet);

    @Transactional
    @Modifying
    @Query("""
       update SalarySlip s set s.dmJobNumber = :jm, s.dmName = :uname, s.dmTime = :checkTime  where s.id in :ids
""")
    void updateDmChecked(String jm, String uname, LocalDateTime checkTime, List<String> ids);

    @Transactional
    @Modifying
    @Query("""
       update SalarySlip s set s.dceoJobNumber = :jm, s.dceoName = :uname, s.dceoTime = :checkTime where s.id in :ids
""")
    void updateDceoChecked(String jm, String uname, LocalDateTime checkTime, List<String> ids);


    @Query(""" 
        select s from SalarySlip s where s.id in :ids and s.isCheck = false
""")
    List<SalarySlip> findUserUncheck(List<String> ids);

    List<SalarySlip> findByMainIdAndDmJobNumber(String mainId, String dmJobNumber, Sort sort);

    List<SalarySlip> findByMainIdAndDceoJobNumber(String mainId, String dceoJobNumber, Sort sort);

    List<SalarySlip> findByMainId(String mainId);

    List<SalarySlip> findByJobNumberAndMainId(String jobNumber, String mainId);

    int countByMainId(String mainId);

}