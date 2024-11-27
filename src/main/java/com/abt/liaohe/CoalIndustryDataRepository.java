package com.abt.liaohe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CoalIndustryDataRepository extends JpaRepository<CoalIndustryData, String> {


    List<CoalIndustryData> findByReportName(String reportName);

    @Modifying
    @Transactional
    void deleteByReportName(String reportName);
}