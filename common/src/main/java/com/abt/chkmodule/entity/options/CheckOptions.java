package com.abt.chkmodule.entity.options;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 组件类型专属配置，与 {@link com.abt.chkmodule.model.CheckComponentType} 对应。
 * JSON 中通过 {@code kind} 区分具体结构，便于序列化/反序列化与前端渲染。
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextInputOptions.class, name = "input"),
        @JsonSubTypes.Type(value = TextareaOptions.class, name = "textarea"),
        @JsonSubTypes.Type(value = NumberInputOptions.class, name = "number"),
        @JsonSubTypes.Type(value = RadioOptions.class, name = "radio"),
        @JsonSubTypes.Type(value = CheckboxOptions.class, name = "checkbox"),
        @JsonSubTypes.Type(value = ImageUploadOptions.class, name = "imageUpload"),
        @JsonSubTypes.Type(value = FileUploadOptions.class, name = "fileUpload"),
})
public interface CheckOptions {
}
