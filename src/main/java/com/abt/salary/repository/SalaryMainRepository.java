package com.abt.salary.repository;

import com.abt.salary.entity.SalaryMain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryMainRepository extends JpaRepository<SalaryMain, String>{

    List<SalaryMain> findByYearMonthAndGroup(String yearMonth, String group);

}