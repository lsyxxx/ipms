package com.abt.wfbak.repository;

import com.abt.wfbak.entity.FlowOperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlowOperationLogRepository extends JpaRepository<FlowOperationLog, String> {
}
