package com.abt.market.repository;

import com.abt.market.entity.SettlementSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 结算汇总表Repository
 */
@Repository
public interface SettlementSummaryRepository extends JpaRepository<SettlementSummary, String> {
    /**
     * 根据结算主表ID删除汇总数据
     * @param mid 结算主表ID
     */
    void deleteByMid(String mid);
}