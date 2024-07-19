package com.abt.sys.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


public interface SysLogService {

    String APPLICATION = "JavaService";
    String CONTENT = "用户访问";
    String TYPE_NAME = "访问日志";

    /**
     * 用户访问记录
     * @param ip ip
     * @param href 访问url
     * @param createName 用户
     */
    void saveUserRequestLog(String ip, String href, String createName);
}
