package com.abt.flow.model;

import com.abt.sys.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 流程状态/审批结果，最细分的状态
 * 用来更新流程引擎的businessState，方便查询
 * 审批结果的粒度小于流程状态，即审批结果可以得到审批/流程状态，但是审批状态不能得到确定的审批结果
 * #####----注意------######
 * #####
 */
@Slf4j
public enum ProcessState {
    //审批状态，已结束
    Finished(0, "Finished", "已结束"),

    //审批状态: 审批中
    Active(1, "Active", "进行中"),
    Deleted(2, "Deleted", "已删除"),
//    Temporary(3, "Temporary", "暂存"),
    /**
     * 暂停流程。无法进行任何操作
     */
    Suspended(4, "Suspended", "已挂起"),
    /**
     * 人为干预终止
     * cancel
     */
    Terminated(5, "Terminated", "终止"),

    /**
     * 流程仅仅启动，第一个任务还未开始。
     * 仅用来更新flowable的businessKey
     */
    Start(6, "Start", "已启动"),

    Cancel(9, "Cancel", "已撤销"),


    ;

    private final int code;
    private final String value;
    private final String description;

    ProcessState(int code, String value, String description) {
        this.code = code;
        this.value = value;
        this.description = description;
    }

    public int code() {
        return this.code;
    }

    public String value() {
        return this.value;
    }

    public String description() {
        return this.description;
    }

    public boolean equal(int code) {
        return code == this.code;
    }


    public static ProcessState of(int code) {
        for (ProcessState c : ProcessState.values()) {
            if (c.code == code) {
                return c;
            }
        }
        throw new IllegalStateException("No such state code : " + code);
    }

    public static ProcessState of(Decision decision) {
        switch (decision) {
            case Approve -> {
                return Active;
            }
            case Reject -> {
                return Finished;
            }

            default -> {
                log.warn("未知的决策: " + decision);
                throw new BusinessException("Unknown decision - " + decision);
            }
        }
    }

    public static ProcessState of(String str) {
        for (ProcessState c : ProcessState.values()) {
            if (c.value().equals(str)) {
                return c;
            }
        }
        throw new IllegalStateException("No such state value : " + str);
    }

}

