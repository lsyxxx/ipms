package com.abt.qrtzjob;

import com.abt.salary.AutoCheckSalaryJob;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 *
 */
@RestController
@Slf4j
@RequestMapping("/test/qrtz")
@Tag(name = "QuartzJobController", description = "")
public class QuartzJobController {

    private final AutoCheckSalaryJob autoCheckSalaryJob;
    private final QuartzJobCreator quartzJobCreator;


    public QuartzJobController(AutoCheckSalaryJob autoCheckSalaryJob, QuartzJobCreator quartzJobCreator) {
        this.autoCheckSalaryJob = autoCheckSalaryJob;
        this.quartzJobCreator = quartzJobCreator;
    }

    @GetMapping("/create")
    public void createJob() throws SchedulerException, ClassNotFoundException {
        autoCheckSalaryJob.createJobAndScheduler(LocalDateTime.now().plusMinutes(1));
    }
}
