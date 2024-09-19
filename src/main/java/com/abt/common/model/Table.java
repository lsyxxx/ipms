package com.abt.common.model;

import com.abt.oa.model.CalendarEvent;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 */
@Data
public class Table {

    /**
     * rows和header列一致
     */
    private List<Row> rows = new ArrayList<>();


    private int month;
    private String company;
    /**
     * yyy-MM格式
     */
    private String yearMonth;

    private List<String> headers;

    private List<CalendarEvent> events;

    private LocalDate start;
    private LocalDate end;


    public void addRow(Row row) {
        this.rows.add(row);
    }


}
