package com.abt.flow.config;

import com.abt.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.cfg.IdGenerator;
import org.flowable.engine.test.Deployment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 自定义流程编号
 * 并发可能会有问题
 */
@Component
@Slf4j
@Deprecated
public class CustomIdGenerator implements IdGenerator {
    @Override
    public synchronized String getNextId() {
        log.trace("开始执行自定义ID生成器....");
        return TimeUtil.idGenerator();
    }
}
