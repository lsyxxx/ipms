package com.abt.flow.model;

/**
 * 自定义流程状态
 */
public enum ProcessState {
    Completed(0, "Completed", "已完成"),
    Active(1, "Active", "进行中"),
    Deleted(2, "Deleted", "已删除"),
//    Temporary(3, "Temporary", "暂存"),
    /**
     * 暂停流程。无法进行任何操作
     */
    Suspended(4, "Suspended", "已挂起"),
    /**
     * 人为干预终止
     */
    Terminated(5, "Terminated", "已终止"),

    /**
     * 流程仅仅启动，第一个任务还未开始。
     * 仅用来更新flowable的businessKey
     */
    Start(6, "Start", "已启动"),

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

    public boolean equal(int code) {
        return code == this.code;
    }

    /**
     * 流程是否结束
     * 包括正常结束Completed和异常结束Terminated
     * @return
     */
    public boolean isFinished() {
        return this == Completed || this == Terminated;
    }

    /**
     * 流程是否正在进行
     * @return
     */
    public boolean isActive() {
        return this == Active;
    }

    public boolean isDeleted() {
        return this == Deleted;
    }

    public static ProcessState of(int code) {
        for (ProcessState c : ProcessState.values()) {
            if (c.code == code) {
                return c;
            }
        }
        throw new IllegalStateException("No such state code : " + code);
    }


}

