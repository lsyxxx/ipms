package com.abt.market.model;

import lombok.Getter;

/**
 * 结算单-保存类型
 */
@Getter
public enum SaveType {
    TEMP("暂存"),
    SAVE("保存"),
    INVALID("作废"),
    INVOICE("已开票"),
    PAYED("已支付")
    ;

    private final String description;

    SaveType(String description) {
        this.description = description;
    }

}
