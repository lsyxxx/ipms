package com.abt.common;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class CommonConstants {
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static final Map<DayOfWeek, String> chinaDayOfWeek = Map.of(
            DayOfWeek.MONDAY, "一",
            DayOfWeek.TUESDAY, "二",
            DayOfWeek.WEDNESDAY, "三",
            DayOfWeek.THURSDAY, "四",
            DayOfWeek.FRIDAY, "五",
            DayOfWeek.SATURDAY, "六",
            DayOfWeek.SUNDAY, "日"
    );

}
