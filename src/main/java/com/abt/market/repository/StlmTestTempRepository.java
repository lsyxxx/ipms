package com.abt.market.repository;

import com.abt.market.entity.SettlementSummary;
import com.abt.market.entity.StlmTestTemp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StlmTestTempRepository extends JpaRepository<StlmTestTemp, String> {
    void deleteAllByTempMid(String tempMid);

    List<StlmTestTemp> findAllByTempMid(String tempMid);
    
    @Query("""
        select new com.abt.market.entity.SettlementSummary(
            s.entrustId,
            s.checkModuleId,
            s.checkModuleName,
             cast(s.price as double),
            count(s),
            cast(sum(s.price) as double)
        )
        from StlmTestTemp s
        where s.tempMid = :tempMid
        group by s.entrustId, s.checkModuleId, s.checkModuleName, s.price
    """)
    List<SettlementSummary> createSummaryDataByTemp(String tempMid);
}