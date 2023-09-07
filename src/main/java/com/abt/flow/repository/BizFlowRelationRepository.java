package com.abt.flow.repository;

import com.abt.flow.model.entity.BizFlowRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 流程-业务关系
 */
@Repository
public interface BizFlowRelationRepository extends JpaRepository<BizFlowRelation, String> {


    Page<BizFlowRelation> findByStarterIdAndCustomNameContainingOrderByStartDateDesc(@Param("startId") String startId, @Param("customName")String customName, Pageable pageable);


    BizFlowRelation findByProcInstId(@Param("procInstId") String procInstId);
}
