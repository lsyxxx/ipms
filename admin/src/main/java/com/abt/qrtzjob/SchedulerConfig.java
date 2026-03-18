package com.abt.qrtzjob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 * quartz-job 配置
 */
@Configuration
@Slf4j
public class SchedulerConfig implements SchedulerFactoryBeanCustomizer {

    private final DataSource dataSource;

    public SchedulerConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {
        log.info("Customizing SchedulerFactoryBean....");
//        schedulerFactoryBean.setDataSource(dataSource);
    }
}
