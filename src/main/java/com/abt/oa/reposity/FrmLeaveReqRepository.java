package com.abt.oa.reposity;

import com.abt.oa.entity.FrmLeaveReq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FrmLeaveReqRepository extends JpaRepository<FrmLeaveReq, String> {

    @Query("select e from FrmLeaveReq e " +
            "where e.applyUserID = :applyUserID " +
            "and e.startDate <= :endDate or e.endDate >= :startDate")
    List<FrmLeaveReq> findByApplyUserIDAndDateBetween(String applyUserID, LocalDate startDate, LocalDate endDate);
}