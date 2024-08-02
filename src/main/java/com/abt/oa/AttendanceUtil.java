package com.abt.oa;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.Month;

/**
 * 考勤
 */
public class AttendanceUtil {

    /**
     * 计算当前时间所属考勤月
     * @param startDay 考勤开始日，如26
     * @return 1-13：13表示跨年
     */
    public static Month currentAttendanceMonth(int startDay) {
        final LocalDate now = LocalDate.now();
        LocalDate startDate = LocalDate.of(now.getYear(), now.getMonth(), startDay);
        if (now.isBefore(startDate)) {
            return now.getMonth();
        }
        return now.getMonth().plus(1);
    }

    public static LocalDate currentStartDate(int startDay) {
        final LocalDate now = LocalDate.now();
        LocalDate startDate = LocalDate.of(now.getYear(), now.getMonth(), startDay);
        //now >= startDate
        if (!now.isBefore(startDate)) {
            return startDate;
        }
        return startDate.minusMonths(1);
    }

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
