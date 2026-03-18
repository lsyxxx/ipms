package com.abt.salary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlipCount {
    private String yearMonth;
    private long totalCount;
    private long checkCount;
    private long uncheckCount;
}
