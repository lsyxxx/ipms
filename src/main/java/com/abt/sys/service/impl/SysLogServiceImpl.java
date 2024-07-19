package com.abt.sys.service.impl;

import com.abt.sys.model.entity.SysLog;
import com.abt.sys.repository.SysLogRepository;
import com.abt.sys.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
@Slf4j
public class SysLogServiceImpl implements SysLogService {

    private final SysLogRepository sysLogRepository;

    public SysLogServiceImpl(SysLogRepository sysLogRepository) {
        this.sysLogRepository = sysLogRepository;
    }

    @Override
    public void saveUserRequestLog(String ip, String href, String createName) {
        SysLog sysLog = new SysLog();
        sysLog.setContent(CONTENT);
        sysLog.setApplication(APPLICATION);
        sysLog.setTypeName(TYPE_NAME);
        sysLog.setIp(ip);
        sysLog.setHref(href);
        sysLog.setCreateName(createName);
        sysLogRepository.save(sysLog);
    }
}
