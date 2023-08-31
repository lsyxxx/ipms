package com.abt.flow.repository;

import com.abt.flow.model.entity.BizFlowRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 流程-业务关系
 */
@Repository
public interface BizFlowRelationRepository extends JpaRepository<BizFlowRelation, String> {




}
