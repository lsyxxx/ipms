package com.abt.flow.config;

import lombok.extern.slf4j.Slf4j;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * 流程相关配置
 */
@Configuration
@Slf4j
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {


    /**
     * 流程图字体
     */
    @Value("${abt.flowable.diagram.font}")
    private String diagramFont;

    /**
     * 流程图比例
     */
    @Value("${abt.flowable.diagram.scaleFactor}")
    private String scaleFactor;


    private final FlowableDataSourceConfigurer configurer;
    public FlowableConfig(FlowableDataSourceConfigurer configurer) {
        this.configurer = configurer;
    }

    /**
     * 导入自定义配置数据源 FlowableDataSourceConfigurer
     * @param engineConfiguration
     */
    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        log.info("将自定义数据库配置导入SpringProcessEngineConfiguration");
        engineConfiguration.addConfigurator(configurer);
    }



}
