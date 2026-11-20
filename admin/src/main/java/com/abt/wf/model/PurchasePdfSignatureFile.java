package com.abt.wf.model;

/** PDF 签名查询结果，仅包含定位图片所需字段，避免加载 Base64 大字段。 */
public record PurchasePdfSignatureFile(String userId, String fileName) {
}
