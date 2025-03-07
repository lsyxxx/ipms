package com.abt.qrtzjob.model;

public enum Status {
    /**
     * 等待执行
     */
    PENDING,
    /**
     * 挂起
     */
    SUSPENDED,
    /**
     * 取消
     */
    CANCELLED,
    /**
     * 执行失败
     */
    FAILED,

    /**
     * 成功完成
     */
    COMPLETED,


}
