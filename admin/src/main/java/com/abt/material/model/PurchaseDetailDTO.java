package com.abt.material.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetailDTO {

    /**
     * 采购流程id
     */
    private String mid;

    /**
     * 物品分类id
     */
    private String materialTypeId;

    /**
     * 物品分类名称
     */
    private String materialTypeName;

    /**
     * 物品id
     */
    private String materialId;

    /**
     * 物品名称
     */
    private String materialName;

    /**
     * 物品规格型号
     */
    private String specification;

    /**
     * 物品单位
     */
    private String unit;

    /**
     * 物品单价
     */
    private BigDecimal price;

    /**
     * 物品数量
     */
    private Integer quantity;

    /**
     * 采购单价合计金额
     */
    private BigDecimal totalPrice;

    /**
     * 采购记录创建日期
     */
    private LocalDateTime createTime;
}
