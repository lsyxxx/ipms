package com.abt.liaohe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MinorElementRepository extends JpaRepository<MinorElement, String> {

    @Modifying
    @Transactional
    void deleteByReportName(String reportName);

    List<MinorElement> findByReportName(String reportName);
}