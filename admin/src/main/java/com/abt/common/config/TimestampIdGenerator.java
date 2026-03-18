package com.abt.common.config;

import com.abt.common.util.TimeUtil;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * 生成时间戳的id
 */
public class TimestampIdGenerator implements IdentifierGenerator {
    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return TimeUtil.idGenerator();
    }

}
