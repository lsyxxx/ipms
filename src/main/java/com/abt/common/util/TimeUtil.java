package com.abt.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        String id = LocalDate.now().format(formatter) + System.currentTimeMillis();
        return id;
    }

}
