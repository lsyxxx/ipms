package com.abt.wf.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** 采购单据导出任务状态。 */
public enum PurchaseDocumentExportStatus {
    QUEUED(0), QUERYING(1), GENERATING(2), PACKAGING(3), COMPLETED(4), FAILED(5);

    private final int code;

    PurchaseDocumentExportStatus(int code) {
        this.code = code;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    @JsonCreator
    public static PurchaseDocumentExportStatus fromCode(int code) {
        for (PurchaseDocumentExportStatus status : values()) {
            if (status.code == code) return status;
        }
        throw new IllegalArgumentException("采购单据导出任务状态不正确");
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }
}
