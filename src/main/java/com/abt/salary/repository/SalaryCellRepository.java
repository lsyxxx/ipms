package com.abt.salary.repository;

import com.abt.salary.entity.SalaryCell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalaryCellRepository extends JpaRepository<SalaryCell, String> {

    List<SalaryCell> findByJobNumberAndYearMonthOrderBySlipIdAscColumnIndexAsc(String jobNumber, String yearMonth);

    List<SalaryCell> findBySlipIdOrderByColumnIndex(String slipId);
    List<SalaryCell> findBySlipIdAndValueIsNotNullOrderByColumnIndexAsc(String slipId);

    @Modifying
    @Query("delete from SalaryCell c where c.mid = :mid")
    int deleteAllByMid(String mid);
}