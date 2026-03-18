package com.abt.common.config;

import com.abt.common.util.TimeUtil;
import lombok.AllArgsConstructor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.util.Properties;

/**
 * 生成时间戳的id
 */
@AllArgsConstructor
public class Timestamp2IdGenerator implements IdentifierGenerator {
    private String prefix;
    private String suffix;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        prefix = params.getProperty("prefix", "");
        suffix = params.getProperty("suffix", "");
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) {
        return prefix + "-" + TimeUtil.idGenerator() + "-" + suffix;
    }

}
