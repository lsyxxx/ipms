package com.abt.chemicals.entity;

import lombok.Data;

/**
 * 化学品分类
 */
@Data
public class ChemicalType {

    private String id;
    private String name;
    private String description;
    private String parentId;
    /**
     * 排序
     */
    private int sort;
    /**
     * 层级
     */
    private int level;
}
