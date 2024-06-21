package com.abt.salary.service;

import com.abt.common.util.TimeUtil;
import com.abt.salary.entity.SalarySlip;
import com.abt.salary.repository.SalarySlipRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


/**
 * 自动确认
 */
@Service
@Slf4j
public class AutoCheckScheduledService {

    private final SalaryService salaryService;

    public AutoCheckScheduledService(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    // 每60秒检查一次
//    @Scheduled(fixedRate = 60000)
    public void checkAndExecuteTasks() {
        log.info("=== AutoCheckScheduledService Start: {}", LocalDateTime.now());
        final long t1 = System.currentTimeMillis();
        final List<SalarySlip> unchecked = salaryService.findSalarySlipUnchecked();
        List<SalarySlip> checked = new ArrayList<>();
        unchecked.forEach(i -> {
            salaryService.getSlipAutoCheckTime(i);
            final boolean equal = TimeUtil.compareWithMin(i.getAutoCheckTime(), LocalDateTime.now());
            if (equal) {
                i.setCheck(true);
                checked.add(i);
            }
        });
        System.out.println("checked: " + checked.size());
        salaryService.saveAllSlip(checked);
        final long t2 = System.currentTimeMillis();
        log.info("AutoCheckScheduledService cost: {}ms", (t2 - t1));
    }

}
