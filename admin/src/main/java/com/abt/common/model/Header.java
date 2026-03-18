package com.abt.common.model;

import lombok.Data;

import java.util.List;

/**
 * 适用于element-ui的表头
 */
@Data
public class Header {
    private String label;
    private String value;
    private int rowIndex;
    private int columnIndex;
    /**
     * 若存在多级标题
     */
    private List<Header> children;
}
