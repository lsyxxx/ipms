package com.abt.oa.model;

import com.abt.common.util.TimeUtil;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 日程事件, 用于fullCalendar
 */
@Data
public class CalendarEvent {

    private String title;
    /**
     * 开始时间,yyyy-mm-dd或yyyy-mm-dd HH:mm:ss
     */
//    private LocalDateTime startTime;
    /**
     * 开始日期/事件字符串，符合fullCalendar要求
     */
    private String start;

//    private LocalDateTime endTime;
    private String end;

    /**
     * 排序
     */
    private int order;

    /**
     * 事件id
     */
    private String id;

    /**
     * 事件背景颜色
     */
    private String backgroundColor;

    /**
     * 字体颜色
     */
    private String textColor;

    /**
     * FullCalendar: event model: display
     */
    private String display;

    /**
     * 类型
     */
    private String type;

    private String shortName;

    /**
     * 日期的天(1-31)
     */
    private int day;

    /**
     * 时长/天数
     */
    private double duration;

    /**
     * 时长单位，小时(hour)/天(day)
     */
    private String durationUnit;

    public static final String DUR_UNIT_HOUR = "hour";
    public static final String DUR_UNIT_DAY = "day";


    public static CalendarEvent createDayBackgroundColor(String start, String backgroundColor) {
        CalendarEvent event = new CalendarEvent();
        event.setDisplay("background");
        event.setBackgroundColor(backgroundColor);
        event.setOrder(0);
        event.setStart(start);
        event.build();
        return event;
    }

    public CalendarEvent build() {
        return this;
    }


}


