package com.abt.oa.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 野外用车生命周期。 */
@Getter
@RequiredArgsConstructor
public enum FieldVehicleStatus {
    DRAFT(0, "草稿"), USING(1, "使用中"), FINISHED(2, "已结束");

    private final int code;
    private final String label;
}
