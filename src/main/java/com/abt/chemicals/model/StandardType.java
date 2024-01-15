package com.abt.chemicals.model;

import lombok.Data;
import lombok.Getter;

/**
 * 标准类型
 */
@Getter
public enum StandardType {
    GB("国标"),
    HB("行标"),
    QB("企标"),
    DB("地方标准")
    ;

    private String code;
    StandardType(String code) {
        this.code = code;
    }
}
