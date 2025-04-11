package com.abt.material.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 汇总出入库数量
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockQuantitySummary {
    /**
     * 物品名称
     */
    private String name;
    private String specification;
    private String unit;
    private Double quantity;
    /**
     * 出入库类型
     */
    private int stockType;

    private String warehouseId;
    private String warehouseName;

}
