package com.abt.oa.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 野外工作用户看板
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldWorkBoard {
    private LocalDate periodStart;
    private LocalDate periodEnd;

    private String userid;
    private String username;

    /**
     * [periodStart,  periodEnd] 天数，包含
     */
    private int dayCount;

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

    /**
     * 请假天数，包含所有请假类型
     */
    private int leaveDay;

    private int todoCount;
    private int doneCount;
    /**
     * 已提交的
     */
    private int applyCount;
    /**
     * 已通过的
     */
    private int passCount;

    /**
     * 作业人数
     */
    private int workerCount;


    private List<CalendarEvent> events = new ArrayList<>();

}
