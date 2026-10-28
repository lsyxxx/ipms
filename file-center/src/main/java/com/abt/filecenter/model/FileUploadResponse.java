package com.abt.filecenter.model;

import lombok.Builder;
import lombok.Getter;

/**
 * MinIO 上传结果；对象键用于后续业务关联，不表示可匿名访问的 URL。
 */
@Getter
@Builder
public class FileUploadResponse {
    private String bucket;
    private String objectKey;
    private String originalName;
    private String contentType;
    private long size;
    private String etag;
}
