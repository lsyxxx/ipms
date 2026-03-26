package com.abt.wxapp.checkmodule.model;

/**
 * 组件类型
 */
public enum CheckComponentType {
    /**
     * 数字输入，input-number
     */
    number,
    /**
     * 单选
     */
    radio,

    /**
     * 多选
     */
    checkbox,

    /**
     * 单行输入
     */
    input,

    /**
     * 富文本
     */
    textarea,

    /**
     * 仅图片上传
     */
    imageUpload,

    /**
     * 文件上传（微信文件）
     */
    fileUpload,
}
