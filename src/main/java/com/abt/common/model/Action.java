package com.abt.common.model;

/**
 * 操作
 */
public enum Action {

    create("create",  "创建"),

    apply("apply", "申请"),

    complete("complete", "完成"),

    undo("undo", "撤销"),
    delete("delete", "删除"),
    audit("audit", "审核"),
    check("check", "复核"),
    suspend("suspend", "挂起"),
    active("active", "激活")
    ;

    private final String value;
    private final String description;

    Action(String value, String description) {
        this.value = value;
        this.description = description;
    }


    public String value() {
        return this.value;
    }

    public String description() {
        return this.description;
    }


}
