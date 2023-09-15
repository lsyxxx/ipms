package com.abt.flow.repository;

import com.abt.flow.model.entity.Reimburse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * 报销流程
 */
public interface ReimburseRepository extends JpaRepository<Reimburse, String> {
    Reimburse findByProcessInstanceId(@Param("processInstanceId") String processInstanceId);
}
