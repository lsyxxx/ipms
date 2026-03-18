package com.abt.wf.service;

import com.abt.wf.entity.FlowOperationLog;

import java.util.List;
import java.util.Optional;

/**
 *
 */
public interface FlowOperationLogService {

    FlowOperationLog saveLog(FlowOperationLog log);

    List<FlowOperationLog> findLogsByEntityId(String entityId);

    Optional<FlowOperationLog> findById(String id);
}
