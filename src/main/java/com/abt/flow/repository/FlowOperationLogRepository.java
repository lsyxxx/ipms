package com.abt.flow.repository;

import com.abt.flow.model.entity.FlowOperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlowOperationLogRepository extends JpaRepository<FlowOperationLog, String> {
}
