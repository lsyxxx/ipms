package com.abt.common.util;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 *
 */
public class TimeUtil {

    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 根据当前时间生成ID
     * yyyyMMdd+timestamp
     * @return
     */
    public static String idGenerator() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.now().format(formatter) + System.currentTimeMillis();
    }

    public static LocalDateTime from(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }


    public static LocalDate from(java.sql.Date sqlDate) {
        if (sqlDate == null) {
            return null;
        }
        return sqlDate.toLocalDate();
    }

    public static LocalDateTime from(Timestamp ts) {
        if (ts == null) {
            return null;
        }
        return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 转为yyyy-MM-dd格式字符串
     * @param localDate 日期
     */
    public static String yyyy_MM_ddString(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(dateFormatter);
    }

    /**
     * 当前周的周一日期
     */
    public static LocalDate startDayOfCurrentWeek() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 当前周的周日日期
     */
    public static LocalDate endDayOfCurrentWeek() {
        LocalDate today = LocalDate.now();
        return today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }

    public static LocalDate startDayOfCurrentMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.of(today.getYear(), today.getMonth());
        return currentMonth.atDay(1);
    }

    public static LocalDate endDayOfCurrentMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.of(today.getYear(), today.getMonth());
        return currentMonth.atEndOfMonth();
    }

    public static LocalDate endDayOfCurrentYear() {
        LocalDate today = LocalDate.now();
        return LocalDate.of(today.getYear(), 12, 31);
    }

    public static LocalDate startDayOfCurrentYear() {
        LocalDate today = LocalDate.now();
        return LocalDate.of(today.getYear(), 1, 1);
    }

    /**
     * 比较2个时间在分钟级别是否先沟通
     */
    public static boolean compareWithMin(LocalDateTime t1, LocalDateTime t2) {
        return ChronoUnit.MINUTES.between(t1, t2) == 0;
    }


}
