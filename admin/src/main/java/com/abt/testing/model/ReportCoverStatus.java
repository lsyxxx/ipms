package com.abt.testing.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 报告封面配置启用状态。 */
@Getter
@RequiredArgsConstructor
public enum ReportCoverStatus {
    DISABLED(0),
    ENABLED(1);

    private final int code;
}
