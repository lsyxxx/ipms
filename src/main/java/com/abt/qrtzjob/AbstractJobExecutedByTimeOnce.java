package com.abt.qrtzjob;

import com.abt.qrtzjob.entity.QuartzJobStore;
import com.abt.qrtzjob.service.QuartzJobService;
import com.abt.sys.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;
import org.quartz.*;

/**
 * 指定时间执行一次的quartz job
 */
public abstract class AbstractJobExecutedByTimeOnce implements Job {

    private final QuartzJobService quartzJobService;

    @Getter
    @Setter
    private QuartzJobStore quartzJobStore;

    public abstract QuartzJobStore createJobStore();

    protected AbstractJobExecutedByTimeOnce(QuartzJobService quartzJobService) {
        this.quartzJobService = quartzJobService;
        this.quartzJobStore = createJobStore();
    }

    /**
     * 默认创建一个只执行一次的quartz job
     */
    public void doCreateJob() throws ClassNotFoundException, SchedulerException {
        if (quartzJobStore == null) {
            throw new BusinessException("QuartzJobStore for " + this.getClass().getName() + " is null");
        }
        final Class<?> clz = Class.forName(quartzJobStore.getJobClassName());
        final JobDetail jobDetail = QuartzJobCreator.createSimpleJobDetail((Class<? extends Job>) clz, quartzJobStore.getJobId(), quartzJobStore.getGroupId());
        final Trigger trigger = QuartzJobCreator.createTriggerExecuteOnce(quartzJobStore.getStartAt(), quartzJobStore.getJobId(), quartzJobStore.getGroupId());
        quartzJobService.jobRegister(jobDetail, trigger, this.quartzJobStore);
    }

}
