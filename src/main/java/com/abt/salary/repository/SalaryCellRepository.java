package com.abt.salary.repository;

import com.abt.salary.entity.SalaryCell;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryCellRepository extends JpaRepository<SalaryCell, String> {

    List<SalaryCell> findByColumnIndex(Integer columnIndex);

    void deleteByMid(String mid);
}