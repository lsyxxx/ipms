package com.abt.market.model;

import lombok.Getter;

/**
 * 结算单-保存类型
 */
@Getter
public enum SaveType {
    TEMP("暂存"),
    SAVE("保存");

    private final String description;

    SaveType(String description) {
        this.description = description;
    }

}
