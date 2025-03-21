package com.abt.salary;

import com.abt.qrtzjob.AbstractJobExecutedByTimeOnce;
import com.abt.qrtzjob.entity.QuartzJobStore;
import com.abt.qrtzjob.model.Status;
import com.abt.qrtzjob.service.QuartzJobService;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.service.SalaryService;
import lombok.Getter;
import lombok.Setter;
import org.quartz.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 自动确认
 */
@Component
//不能并发执行同一个(identity相同)任务
@DisallowConcurrentExecution
public class AutoCheckSalaryJob extends AbstractJobExecutedByTimeOnce {

    public static final String JOB_GROUP = "salarySlip";
    public static final String JOB_ID_PREFIX = "autoCheckSalaryJob";
    public static final String TRIGGER_ID_PREFIX = "autoCheckSalaryTrigger";
    public static final String JOB_NAME = "AutoCheckSalaryJob";

    private final SalaryService salaryService;
    @Getter
    @Setter
    private LocalDateTime startAt;

    public AutoCheckSalaryJob(QuartzJobService quartzJobService, SalaryService salaryService) {
        super(quartzJobService);
        this.salaryService = salaryService;
    }

    public void createJobAndScheduler(LocalDateTime startAt) throws SchedulerException, ClassNotFoundException {
        setStartAt(startAt);
        createJobStore();
        doCreateJob();
    }


    @Override
    public void execute(JobExecutionContext context) {
        final List<SalarySlip> unchecked = salaryService.findSalarySlipUnchecked();
        LocalDateTime now = LocalDateTime.now();
        List<SalarySlip> checked = new ArrayList<>();
        for (SalarySlip slip : unchecked) {
            final LocalDateTime autoCheckTime = slip.getAutoCheckTime();
            if (slip.isForceCheck() && autoCheckTime.isBefore(now)) {
                slip.check(SalarySlip.CHECK_TYPE_AUTO);
                checked.add(slip);
            }
        }
        salaryService.saveAllSlip(checked);
    }

    public QuartzJobStore createJobStore() {
        QuartzJobStore qjs = new QuartzJobStore();
        String yearMonth = "";
        if (this.startAt != null) {
            yearMonth = this.startAt.getYear() + "_" + this.startAt.getMonthValue();
            qjs.setDescription(String.format("工资自动确认_%d年%d月%d日", this.startAt.getYear(), this.startAt.getMonthValue(), this.startAt.getDayOfMonth()));
        } else {
            qjs.setDescription("工资自动确认");
        }
        qjs.setName(JOB_NAME + yearMonth);
        qjs.setJobClassName(this.getClass().getName());
        qjs.setGroupId(JOB_GROUP);
        qjs.setJobId(JOB_ID_PREFIX + UUID.randomUUID().toString());
        qjs.setStatus(Status.PENDING);
        qjs.setTriggerId(TRIGGER_ID_PREFIX + UUID.randomUUID().toString());
        qjs.setStartAt(this.getStartAt());
        setQuartzJobStore(qjs);
        return qjs;
    }

}
