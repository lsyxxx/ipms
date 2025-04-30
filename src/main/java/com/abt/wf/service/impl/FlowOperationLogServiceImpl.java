package com.abt.wf.service.impl;

import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.repository.FlowOperationLogRepository;
import com.abt.wf.service.FlowOperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlowOperationLogServiceImpl implements FlowOperationLogService {
    private final FlowOperationLogRepository flowOperationLogRepository;

    public FlowOperationLogServiceImpl(FlowOperationLogRepository flowOperationLogRepository) {
        this.flowOperationLogRepository = flowOperationLogRepository;
    }

    @Override
    public FlowOperationLog saveLog(FlowOperationLog log) {
        return flowOperationLogRepository.save(log);
    }

    @Override
    public List<FlowOperationLog> findLogsByEntityId(String entityId) {
        return flowOperationLogRepository.findByEntityIdOrderByTaskEndTimeAsc(entityId);
    }

    @Override
    public Optional<FlowOperationLog> findById(String id) {
        return flowOperationLogRepository.findById(id);
    }
}
