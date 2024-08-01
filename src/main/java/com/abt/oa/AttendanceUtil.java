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

    /**
     * 获取当前时间考勤周期
     * @param startDay 开始日
     * @param endDay 结束日
     * @return [开始日期，结束日期]
     */
    public static LocalDate[] currentAttendanceRange(int startDay, int endDay) {
        final LocalDate now = LocalDate.now();
        LocalDate startDate = LocalDate.of(now.getYear(), now.getMonth(), startDay);
        LocalDate endDate = LocalDate.of(now.getYear(), now.getMonth(), endDay);
        if (now.isBefore(startDate)) {
            return new LocalDate[]{startDate.minusMonths(1), endDate};
        } else {
            return new LocalDate[]{startDate, endDate.plusMonths(1)};
        }
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


    public static void main(String[] args) {
        System.out.println(currentStartDate(26));
    }



}
