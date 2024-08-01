package com.abt.oa.reposity;

import com.abt.oa.entity.FrmLeaveReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FrmLeaveReqRepository extends JpaRepository<FrmLeaveReq, String> {

    /**
     * 查询用户在日期范围内（请假时间段全部或部分在指定时间段内）的请假情况
     * @param applyUserID 请假用户
     * @param startDate 起始时间
     * @param endDate 结束时间
     */
    @Query("select e from FrmLeaveReq e " +
            "where e.applyUserID = :applyUserID " +
            "and e.startDate <= :endDate and e.endDate >= :startDate")
    List<FrmLeaveReq> findByApplyUserIDAndDateBetween(String applyUserID, LocalDate startDate, LocalDate endDate);


    @Query("select count(1) from FrmLeaveReq e " +
            "where e.applyUserID = :applyUserID " +
            "and e.startDate <= :endDate and e.endDate >= :startDate")
    int countByApplyUserIDAndDateBetween(String applyUserID, LocalDate startDate, LocalDate endDate);
}