package com.abt.market.repository;

import com.abt.market.entity.SettlementRelation;
import com.abt.market.model.SettlementRelationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRelationRepository extends JpaRepository<SettlementRelation, String> {
    List<SettlementRelation> findByMid(String mid);

    void deleteByMid(String mid);

    void deleteByMidAndBizType(String mid, SettlementRelationType bizType);

    void deleteByMidAndRid(String mid, String rid);
}