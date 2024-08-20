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
    /**
     * 是否是统计列
     */
    private boolean summaryColumn = false;

    public Cell(String valueStr, String columnName) {
        this.valueStr = valueStr;
        this.columnName = columnName;
    }


}
