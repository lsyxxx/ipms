package com.abt.wf.service;

import com.abt.wf.entity.FlowOperationLog;

import java.util.List;

/**
 *
 */
public interface FlowOperationLogService {

    FlowOperationLog saveLog(FlowOperationLog log);

    List<FlowOperationLog> findLogsByEntityId(String entityId);
}
