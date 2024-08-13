package com.abt.common.model;

import com.abt.oa.service.impl.FieldWorkServiceImpl;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Data
public class Row {

    private List<Cell> cells = new ArrayList<>();
    private String username;
    private String jobNumber;
    private int month;
    private String company;


    public void addCell(Cell cell) {
        cells.add(cell);
    }

    public List<Cell> getCellsByColumnName(String columnName) {
        return cells.stream().filter(cell -> cell.getColumnName().equals(columnName)).collect(Collectors.toList());
    }

    public static Row create(String username, String jobNumber, String company) {
        Row row = new Row();
        row.setUsername(username);
        row.setJobNumber(jobNumber);
        row.setCompany(company);
        return row;
    }

}
