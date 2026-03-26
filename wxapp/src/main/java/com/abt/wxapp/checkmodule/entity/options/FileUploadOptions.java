package com.abt.wxapp.checkmodule.entity.options;

import com.abt.wxapp.checkmodule.model.CheckComponentType;
import com.fasterxml.jackson.annotation.JsonProperty;
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
