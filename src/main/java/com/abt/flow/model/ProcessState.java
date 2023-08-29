package com.abt.flow.model;

/**
 * 自定义流程状态
 */
public enum ProcessState {
    Completed(0, "Completed", "已完成"),
    Active(1, "Active", "进行中"),
    Deleted(2, "Deleted", "已删除"),
    Temporary(3, "Temporary", "暂存"),
    /**
     * 暂停流程，停止操作
     */
    Suspended(4, "Suspended", "已挂起"),
    /**
     * 人为干预终止
     */
    Terminated(5, "Terminated", "已终止"),

    ;

    private final int code;
    private final String name;
    private final String description;

    ProcessState(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public int code() {
        return this.code;
    }

    public String description() {
        return this.description;
    }
}

