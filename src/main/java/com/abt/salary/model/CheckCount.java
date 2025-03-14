package com.abt.salary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckCount {
    private String yearMonth;
    private int dmCount;
    private int dceoCount;
    private int allCount;
}
