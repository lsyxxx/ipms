package com.abt.qrtzjob.service;

import com.abt.qrtzjob.entity.QuartzJobStore;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

public interface QuartzJobService {
    /**
     * 注册一个任务
     * 1. 注册quartz job scheduler
     * 2. 保存Job store
     */
    void jobRegister(JobDetail jobDetail, Trigger trigger, QuartzJobStore jobStore) throws SchedulerException;
}
