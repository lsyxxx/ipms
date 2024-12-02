package com.abt.liaohe;

import jakarta.persistence.Transient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MajorElementRepository extends JpaRepository<MajorElement, String> {

    @Modifying
    @Transactional
    void deleteByReportName(String reportName);

    List<MajorElement> findByReportName(String reportName);
}