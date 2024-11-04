package com.abt.liaohe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RawDataRepository extends JpaRepository<RawData, String> {
    List<RawData> findByReportName(String reportName);
    @Transactional
    @Modifying
    void deleteByReportName(String reportName);

    @Transactional
    @Modifying
    @Query("update RawData rd set rd.testName = :newTestName where rd.testName like %:testName%")
    void updateTestName(String testName, String newTestName);


    /**
     * 删除空数据
     */
    @Transactional
    @Modifying
    @Query("delete from RawData where (testName is null or testName = '') and (testValue is null or testValue = '')")
    void deleteEmptyData();

    /**
     * 删除序号
     */
    @Transactional
    @Modifying
    @Query("delete from RawData where testName like '%序号%'")
    void deleteIndexCol();


    List<RawData> findByTestName(String testName);


    /**
     * 将结果中的"\"转为空
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update [dbo].[tmp_raw_data] set testValue = '' where testValue = '/';")
    void updateEmptyTestValue();
}