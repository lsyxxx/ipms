package com.abt.common.config;

import com.abt.qrtzjob.JobCompleteListener;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
@Slf4j
public class QuartzJobConfig {
    private final Scheduler scheduler;
    private final JobCompleteListener jobCompleteListener;

    public QuartzJobConfig(Scheduler scheduler, JobCompleteListener jobCompleteListener) {
        this.scheduler = scheduler;
        this.jobCompleteListener = jobCompleteListener;
    }

    @PostConstruct
    public void init() throws SchedulerException {
        log.info("init quartz job...");
        scheduler.getListenerManager().addJobListener(jobCompleteListener);
    }
}
