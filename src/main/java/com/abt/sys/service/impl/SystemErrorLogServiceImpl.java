package com.abt.sys.service.impl;

import com.abt.sys.model.entity.SystemErrorLog;
import com.abt.sys.repository.SystemErrorLogRepository;
import com.abt.sys.service.SystemErrorLogService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * 系统错误日志
 */
@Service
@RequiredArgsConstructor
public class SystemErrorLogServiceImpl implements SystemErrorLogService {
    private final SystemErrorLogRepository systemErrorLogRepository;

    @Override
    public void saveLog(SystemErrorLog systemErrorLog) {
        systemErrorLogRepository.save(systemErrorLog);
    }

    @Override
    public void solve(@NotNull String id) {
        systemErrorLogRepository.updateSolved(id);
    }

    @Override
    public Page<SystemErrorLog> findPageable(Pageable pageable) {
        return systemErrorLogRepository.findAll(pageable);
    }

}
