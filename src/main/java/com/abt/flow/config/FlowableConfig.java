package com.abt.flow.config;

import com.abt.flow.listener.GlobalLogListener;
import lombok.extern.slf4j.Slf4j;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * 流程相关配置
 */
@Configuration
@Slf4j
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    private final GlobalLogListener globalLogListener;


    private final FlowableDataSourceConfigurer configurer;
    public FlowableConfig(GlobalLogListener globalLogListener, FlowableDataSourceConfigurer configurer) {
        this.globalLogListener = globalLogListener;
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
        log.info("配置自定义事件监听器到SpringProcessEngineConfiguration");
        engineConfiguration.setEventListeners(Collections.singletonList(globalLogListener));

    }





}
