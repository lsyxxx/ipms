package com.abt.wxapp.checkmodule.entity.options;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * {@code fileUpload} 配置
 */
@JsonTypeName("fileUpload")
public final class FileUploadOptions extends AbstractUploadOptions {

    public FileUploadOptions() {
        setMediaType(UploadMediaType.file);
    }
}
