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

    /**
     * 一个委托项目的汇总合计
     * @param entrustId 委托编号
     */
    @Query("""
    select new com.abt.market.entity.SettlementSummary(s.entrustId, s.checkModuleId, s.checkModuleName, sum(s.sampleNum), sum(s.amount))
    from SettlementMain m
    left join SettlementSummary s on m.id = s.mid
    where m.saveType = 'SAVE'
    and s.entrustId = :entrustId
    group by s.entrustId, s.checkModuleId, s.checkModuleName
""")
    List<SettlementSummary> entrustSummary(String entrustId);
}