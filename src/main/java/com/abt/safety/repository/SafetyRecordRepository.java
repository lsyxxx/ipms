package com.abt.safety.repository;

import com.abt.safety.entity.SafetyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface SafetyRecordRepository extends JpaRepository<SafetyRecord, String>, JpaSpecificationExecutor<SafetyRecord> {

    /**
     * 判断该表单当天是否提交过
     * @param d1 当天日期
     * @param d2 第二天
     * @param formId 表单id
     */
    @Query("""
            select count(r) > 0 from SafetyRecord r where r.formId = :formId and r.checkTime >= :d1 and r.checkTime < :d2
            """)
    boolean checkSubmitDuplicated(LocalDateTime d1, LocalDateTime d2, Long formId);

}