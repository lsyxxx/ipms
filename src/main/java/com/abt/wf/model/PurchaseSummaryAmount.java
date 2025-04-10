package com.abt.wf.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 采购合计金额
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseSummaryAmount {
    /**
     * 合计金额
     */
    private BigDecimal totalAmount;

    /**
     * 购买数量
     */
    private Long quantity;

    /**
     * 物品id
     */
    private String detailId;

    /**
     * 物品名称
     */
    private String name;

    /**
     * 规格型号
     */
    private String specification;

    /**
     * 单位
     */
    private String unit;

    /**
     * 单价
     */
    private BigDecimal price;




}
