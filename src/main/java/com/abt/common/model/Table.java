package com.abt.common.model;

import lombok.Data;

import java.util.ArrayList;
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

    private List<String> headers;

    public void addRow(Row row) {
        this.rows.add(row);
    }


}
