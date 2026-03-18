package com.abt.sys.service;

import com.abt.sys.model.entity.SystemErrorLog;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SystemErrorLogService {
    void saveLog(SystemErrorLog systemErrorLog);

    /**
     * 标记为解决
     * @param id id
     */
    void solve(@NotNull String id);

    Page<SystemErrorLog> findPageable(Pageable pageable);
}
