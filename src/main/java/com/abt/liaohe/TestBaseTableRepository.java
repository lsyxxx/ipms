package com.abt.liaohe;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestBaseTableRepository extends JpaRepository<TestBaseTable, String> {

    TestBaseTable findByReportName(String reportName);
}