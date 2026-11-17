package com.abt.wf.model;

import lombok.Data;

/** 采购单据导出进度及结果，不暴露服务器路径与其他用户信息。 */
@Data
public class PurchaseDocumentExportVO {
    private String id;
    private String scope;
    private PurchaseDocumentExportStatus status;
    private long total;
    private long processed;
    private long success;
    private long failed;
    /** 成功导出但未包含验收记录的单据数。 */
    private long withoutAcceptance;
    private String message;
    private String fileName;
    private long fileSize;
    /** 时间戳，单位毫秒。 */
    private long createdAt;
    private long expiresAt;
}
