package com.abt.common.model;

/**
 * 操作
 */
public enum Action {

    create("创建"),

    apply("申请"),

    complete("完成"),

    undo("撤销"),
    delete("删除"),
    audit("审核"),
    check("复核"),
    suspend("挂起"),
    active("激活"),
    ;

    private final String description;

    Action(String description) {
        this.description = description;
    }
}
