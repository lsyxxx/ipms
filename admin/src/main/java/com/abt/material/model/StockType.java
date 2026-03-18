package com.abt.material.model;

import com.abt.material.entity.StockOrder;

/**
 * 出入库类型
 */
public enum StockType {
    IN(StockOrder.STOCK_TYPE_IN, "入库"),
    OUT(StockOrder.STOCK_TYPE_OUT, "出库"),
    CHECK(StockOrder.STOCK_TYPE_CHECK, "盘点")
    ;

    private final int value;
    private final String description;

    StockType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据值获取枚举
     * @param value 枚举值
     * @return StockType枚举，如果找不到返回null
     */
    public static StockType fromValue(int value) {
        for (StockType type : StockType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据值获取描述
     * @param value 枚举值
     * @return 描述文本，如果找不到返回"未知"
     */
    public static String getDescriptionByValue(int value) {
        StockType type = fromValue(value);
        return type != null ? type.getDescription() : "未知";
    }
}
