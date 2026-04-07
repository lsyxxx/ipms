package com.abt.chkmodule.entity.options;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

/**
 * {@code fileUpload} 配置
 */
@Getter
@JsonTypeName("fileUpload")
public final class FileUploadOptions extends AbstractUploadOptions {

    public FileUploadOptions() {
        setMediaType(UploadMediaType.file);
    }
}
