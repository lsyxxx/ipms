package com.abt.flow;

import lombok.Getter;
import lombok.Setter;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 流程相关配置
 */
@Configuration
public class FlowableConfig {

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


    /**
     * 为Flowable配置数据库
     * @param dataSource
     * @return
     */
    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(@Qualifier("flowableDataSource") DataSource dataSource) {
        SpringProcessEngineConfiguration config = new SpringProcessEngineConfiguration();
        config.setDataSource(dataSource);
        return config;
    }


}
