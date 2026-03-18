package com.abt.sys.repository;

import com.abt.sys.model.entity.SysLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysLogRepository extends JpaRepository<SysLog, String> {
}