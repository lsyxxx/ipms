package com.abt.wf.repository;
import com.abt.wf.entity.FlowOperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 */
public interface FlowOperationLogRepository extends JpaRepository<FlowOperationLog, String> {
    List<FlowOperationLog> findByEntityIdOrderBySortAsc(String entityId);
    List<FlowOperationLog> findByEntityIdOrderByTaskStartTimeAsc(String entityId);
}
