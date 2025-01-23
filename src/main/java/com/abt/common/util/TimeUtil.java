package com.abt.common.util;

import com.abt.common.CommonConstants;
import com.abt.sys.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Slf4j
public class TimeUtil {

    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter yyyy_MM_dd_HH_mm_ss_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter HH_mm_ss_formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter yyyy_MM_formatter = DateTimeFormatter.ofPattern("yyyy-MM");

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

    public static String yyyy_MM_dd_HH_mm_ssString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.format(yyyy_MM_dd_HH_mm_ss_formatter);
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

    public static LocalDateTime toLocalDateTime(String yyyy_MM_dd) {
        if (StringUtils.isBlank(yyyy_MM_dd)) {
            return null;
        }
        LocalDate localDate = LocalDate.parse(yyyy_MM_dd, dateFormatter);
        return localDate.atStartOfDay();
    }


    public static LocalDate toLocalDate(String yyyy_MM_dd) {
        if (StringUtils.isBlank(yyyy_MM_dd)) {
            return null;
        }
        return LocalDate.parse(yyyy_MM_dd, dateFormatter);
    }

    /**
     * 将日期和时间合并
     * @param date 日期
     * @param time hh:mm:ss
     */
    public static LocalDateTime toLocalDateTime(LocalDate date, String time) {
        final boolean isValid = isValidateDefaultTimeFormat(time);
        if (!isValid) {
            throw new BusinessException("时间格式不符合要求(要求格式: " + DEFAULT_TIME_FORMAT + ")");
        }
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT);
        LocalTime localTime = LocalTime.parse(time, timeFormatter);
        return LocalDateTime.of(date, localTime);
    }


    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 默认时间格式(HH:mm:ss)
     */
    public static boolean isValidateDefaultTimeFormat(String time) {
        return isValidTimeFormat(time, DEFAULT_TIME_FORMAT);
    }

    public static boolean isValidTimeFormat(String time, String format) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(format);
        try {
            LocalTime.parse(time, timeFormatter);
            return true;
        } catch (DateTimeParseException e) {
            log.error("时间格式转换错误!", e);
            return false;
        }
    }

    /**
     * 将HH:mm:ss字符串类型的时间转为LocalTime
     * @param hhmmssStr 时间，必须是HH:mm:ss
     */
    public static LocalTime toLocalTime(String hhmmssStr) {
        return LocalTime.parse(hhmmssStr, HH_mm_ss_formatter);
    }

    public static YearMonth toYearMonth(String yyyy_MM) {
        return YearMonth.parse(yyyy_MM, yyyy_MM_formatter);
    }

    public static String chinaDayOfWeek(DayOfWeek dayOfWeek) {
        Assert.notNull(dayOfWeek, "Parameter(dayOfWeek) must not be null!");
        return CommonConstants.chinaDayOfWeek.get(dayOfWeek);
    }

    /**
     * 将时间转为yyyy-mm-dd格式
     * @param dateTime 日期时间
     * @return yyyy-mm-dd字符串
     */
    public static String toYYYY_MM_DDString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        final LocalDate localDate = dateTime.toLocalDate();
        return localDate.format(dateFormatter);
    }

    /**
     * 将时间转为yyyy-mm-dd格式
     * @param dateTime 日期时间
     * @return yyyy-mm-dd字符串
     */
    public static String toYYYY_MM_DDString(LocalDate dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(dateFormatter);
    }



}
