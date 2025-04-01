package com.abt.salary.repository;

import com.abt.salary.entity.SalarySlip;
import com.abt.salary.model.SlipCount;
import jakarta.persistence.NamedEntityGraph;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
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

//    @Query("""
//        select sl from SalarySlip sl left join EmployeeInfo e on sl.jobNumber = e.jobNumber
//        where sl.jobNumber = :jobNumber and sl.yearMonth like %:yearMonthLike%
//""")
    List<SalarySlip> findByJobNumberAndYearMonthContaining(String jobNumber, String yearMonthLike);

//    @Query("""
//       select sl from SalarySlip sl left join EmployeeInfo e on sl.jobNumber = e.jobNumber
//       where sl.jobNumber = :jobNumber and sl.yearMonth like %:yearMonthLike%
//""")
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

    @Transactional
    @Modifying
    @Query("""
       update SalarySlip s set s.hrJobNumber = :jm, s.hrName = :uname, s.hrTime = :checkTime where s.id in :ids
""")
    void updateHrChecked(String jm, String uname, LocalDateTime checkTime, List<String> ids);

    @Transactional
    @Modifying
    @Query("""
       update SalarySlip s set s.ceoJobNumber = :jm, s.ceoName = :uname, s.ceoTime = :checkTime where s.id in :ids
""")
    void updateCeoCheck(String jm, String uname, LocalDateTime checkTime, List<String> ids);


    @Query(""" 
        select s from SalarySlip s where s.id in :ids and s.isCheck = false
""")
    List<SalarySlip> findUserUncheck(List<String> ids);

    List<SalarySlip> findByMainIdAndDmJobNumber(String mainId, String dmJobNumber, Sort sort);

    List<SalarySlip> findByMainIdAndDceoJobNumber(String mainId, String dceoJobNumber, Sort sort);

    List<SalarySlip> findByMainId(String mainId);

    List<SalarySlip> findByJobNumberAndMainId(String jobNumber, String mainId);

    int countByMainId(String mainId);

    List<SalarySlip> findByMainIdIn(Collection<String> mainIds);


    @Query("""
        select new com.abt.salary.model.SlipCount(ss.yearMonth, count(1), count(ss.ceoTime), count(1)-count(ss.ceoTime)) from SalarySlip ss
        where 1=1 and (:yearMonth is null or :yearMonth = '' or ss.yearMonth = :yearMonth)
        group by ss.yearMonth
        order by ss.yearMonth desc
""")
    List<SlipCount> countCeoCheck(String yearMonth);

    @Query("""
        select new com.abt.salary.model.SlipCount(ss.yearMonth, count(1), count(ss.hrTime), count(1)-count(ss.hrTime)) from SalarySlip ss
        where 1=1 and (:yearMonth is null or :yearMonth = '' or ss.yearMonth = :yearMonth)
        group by ss.yearMonth
        order by ss.yearMonth desc
""")
    List<SlipCount> countHrCheck(String yearMonth);

    @Query("""
        select new com.abt.salary.model.SlipCount(ss.yearMonth, count(1), count(ss.dceoTime),  count(1)-count(ss.dceoTime)) from SalarySlip ss
        where 1=1 and (:yearMonth is null or :yearMonth = '' or ss.yearMonth = :yearMonth) and ss.dceoJobNumber = :jobNumber
        group by ss.yearMonth
        order by ss.yearMonth desc
""")
    List<SlipCount> countDceoCheck(String jobNumber, String yearMonth);

    @Query("""
        select new com.abt.salary.model.SlipCount(ss.yearMonth, count(1), count(ss.dmTime), count(1)-count(ss.dmTime)) from SalarySlip ss
        where 1=1 and (:yearMonth is null or :yearMonth = '' or ss.yearMonth = :yearMonth) and ss.dmJobNumber = :jobNumber
        group by ss.yearMonth
        order by ss.yearMonth desc
""")
    List<SlipCount> countDmCheck(String jobNumber, String yearMonth);

    @Query("""
        select new com.abt.salary.model.SlipCount(ss.yearMonth, count(1), count(ss.isCheck), count(1)-count(ss.isCheck)) from SalarySlip ss
        where 1=1 and (:yearMonth is null or :yearMonth = '' or ss.yearMonth = :yearMonth) and ss.jobNumber = :jobNumber
        group by ss.yearMonth
        order by ss.yearMonth desc
""")
    List<SlipCount> countUserCheck(String jobNumber, String yearMonth);

    @Query("""
        select new com.abt.salary.model.SlipCount(ss.yearMonth, count(1), 0, 0) from SalarySlip ss
        where 1=1 and (:yearMonth is null or :yearMonth = '' or ss.yearMonth = :yearMonth)
        group by ss.yearMonth
        order by ss.yearMonth desc
""")
    List<SlipCount> countAllCheck(String yearMonth);

    @Modifying
    @Transactional
    @Query("""
        update SalarySlip s set s.isCheck = false, s.checkTime = null, s.autoCheckTime = :autoCheckTime  where s.id = :slipId
""")
    Integer updateSendById(String slipId, LocalDateTime autoCheckTime);

}