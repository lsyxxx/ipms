package com.abt.qrtzjob.service.impl;

import com.abt.qrtzjob.job.SalaryAutoConfirmJob;
import com.abt.qrtzjob.service.QuartzJobService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 *
 */
@Service
public class QuartzJobServiceImpl implements QuartzJobService {

    private final Scheduler scheduler;

    public QuartzJobServiceImpl(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    @Override
    public void createSalaryAutoConfirmJob(String salarySlipId, LocalDateTime executeTime) throws SchedulerException {
        // 生成唯一Key
        String jobId = UUID.randomUUID().toString();

        JobDetail jobDetail = JobBuilder.newJob(SalaryAutoConfirmJob.class)
                .withIdentity("salaryAutoConfirmJob_" + jobId, "salaryAutoConfirmGroup")
                .usingJobData("salarySlipId", salarySlipId)
                .storeDurably()  // 任务持久化
                .build();

        final SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("salaryAutoConfirmTrigger_" + jobId, "salaryAutoConfirmGroup")
                .startAt(Date.from(executeTime.atZone(ZoneId.systemDefault()).toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        //只执行一次
                        .withRepeatCount(0)
                        .withMisfireHandlingInstructionFireNow()) // 若错过则立即触发
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    @Override
    public List<String> listPendingJobs() throws SchedulerException {
        List<String> pendingJobs = new ArrayList<>();

        // 遍历所有 Trigger 分组
        for (String groupName : scheduler.getTriggerGroupNames()) {
            // 获取分组中的所有 Trigger
            listJobByGroup(groupName);
        }

        return pendingJobs;
    }

    public void listJobByGroup(String groupName) throws SchedulerException {
        for (TriggerKey triggerKey : scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(groupName))) {
            Trigger trigger = scheduler.getTrigger(triggerKey);
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);

            // 筛选未执行的任务（状态为 NORMAL 且有下一次执行时间）
            if (triggerState == Trigger.TriggerState.NORMAL && trigger.getNextFireTime() != null) {
                JobKey jobKey = trigger.getJobKey();
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);

                String jobInfo = String.format("Job Name: %s, Job Group: %s, Trigger: %s, State: %s, Next Fire Time: %s",
                        jobKey.getName(), jobKey.getGroup(), triggerKey, triggerState, trigger.getNextFireTime());
                System.out.println(jobInfo);
            }
        }


    }

    
}
