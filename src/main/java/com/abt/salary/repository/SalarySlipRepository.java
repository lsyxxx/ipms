package com.abt.salary.repository;

import com.abt.salary.entity.SalarySlip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalarySlipRepository extends JpaRepository<SalarySlip, String> {

    List<SalarySlip> findByMainIdOrderByJobNumberAsc(String mainId);

    void deleteByMainId(String mainId);

    int countByIsSendAndMainId(boolean isSend, String mainId);
}