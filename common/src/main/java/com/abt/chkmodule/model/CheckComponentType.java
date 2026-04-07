package com.abt.chkmodule.model;

/**
 * 动态表单组件类型（与 {@link com.abt.chkmodule.entity.CheckComponent} 对应）
 */
public enum CheckComponentType {
    /** 数字输入 */
    number,
    /** 单选 */
    radio,
    /** 多选 */
    checkbox,
    /** 单行输入 */
    input,
    /** 多行 / 富文本 */
    textarea,
    /** 仅图片上传 */
    imageUpload,
    /** 文件上传（微信文件） */
    fileUpload,
}
