package com.abt.salary.repository;

import com.abt.salary.entity.SalaryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryDetailRepository extends JpaRepository<SalaryDetail, String> {

    void deleteByMainId(String mainId);
}