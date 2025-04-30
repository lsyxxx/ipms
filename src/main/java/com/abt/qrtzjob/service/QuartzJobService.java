package com.abt.qrtzjob.service;

import org.quartz.SchedulerException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface QuartzJobService {
    /**
     * 创建定时任务
     * @param salarySlipId 工资条ID
     * @param executeTime 执行时间
     */
    void createSalaryAutoConfirmJob(String salarySlipId, LocalDateTime executeTime) throws SchedulerException;

    List<String> listPendingJobs() throws SchedulerException;
}
