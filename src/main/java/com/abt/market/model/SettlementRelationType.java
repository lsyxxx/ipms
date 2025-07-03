package com.abt.market.model;

import lombok.Getter;

/**
 * 关联结算单业务类型
 */
@Getter
public enum SettlementRelationType {
    AGREEMENT("合同"),
    INVOICE("开票"),
    ;
    private final String description;

    SettlementRelationType(String description) {
        this.description = description;
    }

}
