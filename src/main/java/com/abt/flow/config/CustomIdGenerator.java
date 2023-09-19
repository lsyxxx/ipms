package com.abt.flow.config;

import com.abt.common.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义流程业务数据表ID
 * 并发可能会有问题
 */
@Component
@Slf4j
public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
//        log.trace("开始执行自定义ID生成器....");
        return TimeUtil.idGenerator();
    }
}
