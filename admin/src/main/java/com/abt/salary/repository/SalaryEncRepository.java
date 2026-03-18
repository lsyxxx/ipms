package com.abt.salary.repository;

import com.abt.salary.entity.SalaryEnc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryEncRepository extends JpaRepository<SalaryEnc, String> {
    SalaryEnc findByJobNumber(String jobNumber);
}