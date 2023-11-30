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


    public static LocalDateTime from(java.sql.Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime from(Timestamp ts) {
        if (ts == null) {
            return null;
        }
        return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
