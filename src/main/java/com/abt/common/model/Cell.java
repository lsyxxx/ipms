package com.abt.common.model;

import com.abt.oa.model.CalendarEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
public class Cell {

    //key: 列, value: 数据
    private Object value;
    private String columnName;
    private int columnIndex;
    private String valueStr;
    private String styleStr;
    private Object valueObj;
    /**
     * 是否是统计列
     */
    private boolean summaryColumn = false;

    /**
     * 是否是日期列
     */
    private boolean isDateColumn = false;

    public Cell(String valueStr, String columnName) {
        this.valueStr = valueStr;
        this.columnName = columnName;
    }

    public Cell(String valueStr, String columnName, Object valueObj) {
        this.valueStr = valueStr;
        this.columnName = columnName;
        this.valueObj = valueObj;
    }

    public Cell(String valueStr, String columnName, int columnIndex) {
        this.valueStr = valueStr;
        this.columnName = columnName;
        this.columnIndex = columnIndex;
    }

    public Cell(Object valueObj, String columnName) {
        this.valueObj = valueObj;
        this.valueStr = String.valueOf(valueObj);
        this.columnName = columnName;
    }


}
