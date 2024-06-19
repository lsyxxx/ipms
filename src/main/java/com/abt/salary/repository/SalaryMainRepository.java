package com.abt.salary.repository;

import com.abt.salary.entity.SalaryMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaryMainRepository extends JpaRepository<SalaryMain, String> {


    @Query("select m from SalaryMain m " +
            "where (m.yearMonth IS NULL or  m.yearMonth = :yearMonth) " +
            "order by m.createDate desc")
    List<SalaryMain> findByYearMonthNullable(@Param("yearMonth") String yearMonth);
}