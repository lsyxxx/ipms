package com.abt.wf.model;


import lombok.Getter;

@Getter
public enum ActionEnum {
    SUBMIT("提交"),
    APPROVE("同意"),
    REJECT("拒绝")
    ;
    private String action;
    ActionEnum(String action) {
        this.action = action;
    }
}
