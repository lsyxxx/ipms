package com.abt.salary.repository;

import com.abt.salary.entity.SalaryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalaryDetailRepository extends JpaRepository<SalaryDetail, String> {

    void deleteByMainId(String mainId);

    @Query("select sd.name from SalaryDetail sd where sd.mainId = :mainId group by sd.name having count(sd.name) > 1")
    List<String> countDuplicatedName(String mainId);

    List<SalaryDetail> findByMainIdAndName(String mainId, String name);
}