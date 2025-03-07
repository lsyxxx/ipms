package com.abt.qrtzjob;

import com.abt.qrtzjob.entity.QuartzJobStore;
import com.abt.qrtzjob.repository.QuartzJobStoreRepository;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * 动态创建quartz Job
 */
@Service
public class QuartzJobCreator {

    private final Scheduler scheduler;
    private final QuartzJobStoreRepository quartzJobStoreRepository;

    public QuartzJobCreator(Scheduler scheduler, QuartzJobStoreRepository quartzJobStoreRepository) {
        this.scheduler = scheduler;
        this.quartzJobStoreRepository = quartzJobStoreRepository;
    }

    public void createScheduleJob(JobDetail jobDetail, Trigger trigger, QuartzJobStore jobStore) throws SchedulerException {
        // 将任务和触发器注册到调度器
        scheduler.scheduleJob(jobDetail, trigger);

        //job store
        quartzJobStoreRepository.save(jobStore);
    }

    /**
     * 创建一个只执行一次的触发器
     * @param executeTime 执行时间
     */
    public static Trigger createTriggerExecuteOnce(LocalDateTime executeTime, String triggerId, String groupId) {
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerId, groupId)
                .startAt(Date.from(executeTime.atZone(ZoneId.systemDefault()).toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        //只执行一次
                        .withRepeatCount(0)
                        .withMisfireHandlingInstructionFireNow()) // 若错过则立即触发
                .build();
    }

    public static JobDetail createSimpleJobDetail(Class<? extends Job> jobClz, String jobId, String groupId) {
        return JobBuilder.newJob(jobClz)
                .withIdentity(jobId, groupId)
                .build();
    }



}
