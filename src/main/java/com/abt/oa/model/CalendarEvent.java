package com.abt.oa.model;

import com.abt.common.util.TimeUtil;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日程事件, 用于fullCalendar
 */
@Data
public class CalendarEvent {

    private String title;
    /**
     * 开始时间,yyyy-mm-dd HH:mm:ss
     */
    private LocalDateTime startTime;
    /**
     * 开始日期/事件字符串，符合fullCalendar要求
     */
    private String start;

    private LocalDateTime endTime;
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


    public static CalendarEvent createDayBackgroundColor(LocalDate start, String backgroundColor) {
        CalendarEvent event = new CalendarEvent();
        event.setDisplay("background");
        event.setBackgroundColor(backgroundColor);
        event.setOrder(0);
        event.setStartTime(start.atStartOfDay());
        event.build();
        return event;
    }

    public CalendarEvent build() {
        if (this.startTime != null) {
            this.setStart(TimeUtil.yyyy_MM_dd_HH_mm_ssString(this.startTime));
        }
        if (this.endTime != null) {
            this.setEnd(TimeUtil.yyyy_MM_dd_HH_mm_ssString(this.endTime));
        }
        return this;
    }


}


