package com.abt.flow.config;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.EngineConfigurator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 自定义流程引擎数据库配置
 */
@Component
@Slf4j
public class FlowableDataSourceConfigurer implements EngineConfigurator {

    private final DataSource flowableDataSource;
    private final static int PRIORITY = 100;

    public FlowableDataSourceConfigurer(@Qualifier("flowableDataSource") DataSource flowableDataSource) {
        this.flowableDataSource = flowableDataSource;
    }

    @Override
    public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
        log.info("执行Flowable 数据库配置 beforeInit(engineConfiguration), 优先级: {}", PRIORITY);
        engineConfiguration.setDataSource(flowableDataSource);

    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {

    }

    @Override
    public int getPriority() {
        //执行优先级-flowable最高是50000
        return PRIORITY;
    }
}
