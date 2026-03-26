package com.abt.wxapp.checkmodule.entity.options;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * {@code imageUpload} 配置
 */
@JsonTypeName("imageUpload")
public final class ImageUploadOptions extends AbstractUploadOptions {

    public ImageUploadOptions() {
        setMediaType(UploadMediaType.image);
    }
}
