package com.abt.safety.model;

/**
 * 安全检查流程状态
 * submitted --> completed(检查无问题，结束)
 * submitted --> dispatched --> rectified --> completed(确认整改，结束)
 */
public enum RecordStatus {
    /**
     * 检查表单提交
     */
    SUBMITTED,
    /**
     * 已分配给负责人
     */
    DISPATCHED,
    /**
     * 已整改
     */
    RECTIFIED,

    /**
     * 完成
     */
    COMPLETED,
}
