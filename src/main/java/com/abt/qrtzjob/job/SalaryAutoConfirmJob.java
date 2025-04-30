package com.abt.qrtzjob.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * 工资条自动确认job任务
 */
@DisallowConcurrentExecution
@Component
public class SalaryAutoConfirmJob extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // 获取参数
        String salarySlipId = context.getJobDetail().getJobDataMap().getString("salarySlipId");

        // TODO: 调用业务逻辑，自动确认该工资条
        System.out.println("开始自动确认工资条，工资条ID：" + salarySlipId);
    }
}
