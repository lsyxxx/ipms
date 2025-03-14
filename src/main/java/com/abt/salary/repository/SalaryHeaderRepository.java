package com.abt.salary.repository;

import com.abt.salary.entity.SalaryHeader;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryHeaderRepository extends JpaRepository<SalaryHeader, String> {

    int deleteByMid(String mid);

    List<SalaryHeader> findByMidAndStartRowOrderByStartColumnAsc(String mid, int startRow);


    List<SalaryHeader> findByMidOrderByStartRowAscStartColumnAsc( String mid);


}