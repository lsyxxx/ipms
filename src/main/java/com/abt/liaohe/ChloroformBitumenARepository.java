package com.abt.liaohe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChloroformBitumenARepository extends JpaRepository<ChloroformBitumenA, String> {

    @Modifying
    @Transactional
    void deleteByReportName(String reportName);

    List<ChloroformBitumenA> findByReportName(String reportName);
}