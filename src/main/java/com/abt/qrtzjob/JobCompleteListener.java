package com.abt.qrtzjob;

import com.abt.qrtzjob.entity.QuartzJobStore;
import com.abt.qrtzjob.model.Status;
import com.abt.qrtzjob.service.QuartzJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * quartz job 结束后更新jobstore
 */
@Component
@Slf4j
public class JobCompleteListener implements JobListener {
    private final QuartzJobService quartzJobService;

    public JobCompleteListener(QuartzJobService quartzJobService) {
        this.quartzJobService = quartzJobService;
    }

    @Override
    public String getName() {
        return "JobCompleteListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

    }

    /**
     * 任务被否决
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        log.info("任务执行完成: {}", context.getJobDetail().getKey());
        String id = context.getJobDetail().getKey().getName();
        QuartzJobStore byId = quartzJobService.findById(id);
        if (byId != null) {
            byId.setEndAt(LocalDateTime.now());
            byId.setStatus(Status.COMPLETED);
            byId.addCount();
        }
        quartzJobService.saveJobStore(byId);

    }
}
