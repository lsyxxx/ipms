package com.abt.oa;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.Month;

/**
 * 考勤
 */
public class AttendanceUtil {

    /**
     * 当前日期的考勤月的开始日期
     */
    public static LocalDate currentStartDate(int startDay) {
        final LocalDate now = LocalDate.now();
        LocalDate startDate = LocalDate.of(now.getYear(), now.getMonth(), startDay);
        //now >= startDate
        if (!now.isBefore(startDate)) {
            return startDate;
        }
        return startDate.minusMonths(1);
    }

    /**
     * 当前日期的考勤月的结束日期
     */
    public static LocalDate currentEndDate(int endDay) {
        final LocalDate now = LocalDate.now();
        LocalDate endDate = LocalDate.of(now.getYear(), now.getMonth(), endDay);
        //now <= endDate
        if (!now.isAfter(endDate)) {
            return endDate;
        }
        return endDate.plusMonths(1);
    }


}
