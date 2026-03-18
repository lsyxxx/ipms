package com.abt.wf.model;


import lombok.Getter;

@Getter
public enum ActionEnum {

    APPLY("申请"),
    APPROVE("审批"),
    PASS("同意"),
    REJECT("拒绝"),
    REVOKE("撤回"),
    DELETE("删除"),
    COPY("抄送"),
    ASSIGN("转交"),
    AUTOPASS("系统自动通过")
    ;
    private String action;
    ActionEnum(String action) {
        this.action = action;
    }
}
