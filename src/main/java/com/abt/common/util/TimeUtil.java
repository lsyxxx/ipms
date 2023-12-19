package com.abt.common.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        System.out.println(date.toString());
    }

}
