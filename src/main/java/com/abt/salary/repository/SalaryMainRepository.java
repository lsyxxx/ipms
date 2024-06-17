package com.abt.salary.repository;

import com.abt.salary.entity.SalaryMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaryMainRepository extends JpaRepository<SalaryMain, String> {


    @Query("select m from SalaryMain m " +
            "where (:yearMonth IS NULL or  m.yearMonth = :yearMonth) " +
            "and (:group is null or m.group = :group) " +
            "order by m.createDate desc")
    List<SalaryMain> findByYearMonthAndGroupNullable(@Param("yearMonth") String yearMonth, @Param("group") String group);
}