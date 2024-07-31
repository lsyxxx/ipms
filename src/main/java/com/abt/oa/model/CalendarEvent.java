package com.abt.oa.model;

import com.abt.oa.entity.FieldWorkItem;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日程事件, 用于fullCalendar
 */
@Data
public class CalendarEvent {

    private String title;
    /**
     * 开始时间,yyyy-mm-dd
     */
    private LocalDateTime startTime;

    /**
     * 排序
     */
    private int order;

    /**
     * 事件id
     */
    private String id;

    public static CalendarEvent create(FieldWorkItem item, LocalDateTime startTime) {
        CalendarEvent event = new CalendarEvent();
        if (item == null) {
            return event;
        }

        event.setId(item.getId());
        event.setTitle(item.getAllowanceName());
        event.setOrder(item.getSort());
        event.setStartTime(startTime);

        return event;
    }

}
