package com.abt.wxapp.checkmodule.entity.options;


import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

/**
 * {@code imageUpload} 配置
 */
@Getter
@JsonTypeName("imageUpload")
public final class ImageUploadOptions extends AbstractUploadOptions {

    public ImageUploadOptions() {
        setMediaType(UploadMediaType.image);
    }
}
