package com.abt.wxapp.checkmodule.entity.options;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 上传组件公共字段：{@code imageUpload} / {@code fileUpload} 共用
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractUploadOptions implements CheckOptions {

    /** 图片或文件 */
    private UploadMediaType mediaType;

    /** 最大上传数量 */
    private Integer max;

    /** 单文件大小上限（MB） */
    private Double sizeLimit;

    /**
     * 允许的类型，如 xlsx、word、pdf 等
     * 多个用逗号分隔.
     */
    private String accept;

    private String placeholder;
}
