package com.abt.workflow.model;


import lombok.Data;

/**
 * 类型
 */
public enum NodeTypeEnum {
    //---- 节点类型
    USER("user", "用户节点", UserNode.class),


    //--- 流程类型
    AUDIT("audit", "审批流程", Process.class),
    ;


    private String code;
    private String name;

    private Class<? extends BaseNode> clazz;


    NodeTypeEnum(String code, String name, Class<? extends BaseNode> clazz) {
        this.code = code;
        this.name = name;
        this.clazz = clazz;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends BaseNode> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends BaseNode> clazz) {
        this.clazz = clazz;
    }
}
