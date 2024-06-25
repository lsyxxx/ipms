package com.abt.salary.repository;

import com.abt.salary.entity.SalaryHeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryHeaderRepository extends JpaRepository<SalaryHeader, String> {

    int deleteByMid(String mid);

    List<SalaryHeader> findByMidAndStartRowOrderByStartColumnAsc(String mid, int startRow);



}