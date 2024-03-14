package com.abt.wfbak.service.impl;

import com.abt.wfbak.entity.FlowOperationLog;
import com.abt.wfbak.repository.FlowOperationLogRepository;
import com.abt.wfbak.service.FlowOperationLogService;
import org.springframework.stereotype.Service;

/**
 * 流程记录
 */
@Service
public class FlowOperationLogServiceImpl implements FlowOperationLogService {
    private FlowOperationLogRepository flowOperationLogRepository;
    @Override
    public FlowOperationLog saveLog(FlowOperationLog log) {
        return flowOperationLogRepository.save(log);
    }
}
