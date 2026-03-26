package com.abt.wxapp.user.invoice.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发票抬头类型
 */
@Getter
@AllArgsConstructor
public enum TitleTypeEnum {

    ENTERPRISE(0), // 单位
    PERSONAL(1);   // 个人

    @JsonValue
    private final int code;
}