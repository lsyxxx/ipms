package com.abt.oa.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 野外工作用户看板
 */
@Data
public class FieldWorkUserBoard {
    private LocalDate periodStart;
    private LocalDate periodEnd;

    /**
     * 出勤天数
     */
    private int workDay;
    /**
     * 调休天数
     */
    private int restDay;
    /**
     * 公休天数
     */
    private int officialDay;

    /**
     * 总天数
     */
    private int totalDay;


    private List<CalendarEvent> events = new ArrayList<>();

}
