package com.abt.salary.repository;

import com.abt.salary.entity.SalarySlip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalarySlipRepository extends JpaRepository<SalarySlip, String> {
//    List<SalarySlip> findByMainIdAndErrorIsNotEmpty(String mainId);
    List<SalarySlip> findByMainId(String mainId);
//    List<SalarySlip> findByMainIdAndSend(String mainId, boolean isSend);

//    int countByIsSendAndMainId(boolean isSend, String mainId);
//    int countByIsCheckAndMainId(boolean isCheck, String mainId);
//    int countByIsReadAndMainId(boolean isRead, String mainId);
}