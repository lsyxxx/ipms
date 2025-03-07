package com.abt.qrtzjob.service.impl;

import com.abt.qrtzjob.QuartzJobCreator;
import com.abt.qrtzjob.entity.QuartzJobStore;
import com.abt.qrtzjob.repository.QuartzJobStoreRepository;
import com.abt.qrtzjob.service.QuartzJobService;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class QuartzJobServiceImpl implements QuartzJobService {
    private final QuartzJobStoreRepository quartzJobStoreRepository;
    private final QuartzJobCreator quartzJobCreator;

    public QuartzJobServiceImpl(QuartzJobStoreRepository quartzJobStoreRepository, QuartzJobCreator quartzJobCreator) {
        this.quartzJobStoreRepository = quartzJobStoreRepository;
        this.quartzJobCreator = quartzJobCreator;
    }


    @Override
    public void jobRegister(JobDetail jobDetail, Trigger trigger, QuartzJobStore jobStore) throws SchedulerException {
        quartzJobCreator.createScheduleJob(jobDetail, trigger, jobStore);
    }

    public void saveJobStore(QuartzJobStore quartzJobStore) {
        quartzJobStoreRepository.save(quartzJobStore);
    }
}
