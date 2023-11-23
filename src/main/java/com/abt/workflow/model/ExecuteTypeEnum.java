package com.abt.workflow.model;

/**
 * 处理方式
 */
public enum ExecuteTypeEnum {
    SEQUENCE("sequence", "依次处理(按顺序同意或拒绝)"),
    AND("and", "会签(所有人同意才可以通过)"),
    OR("or", "或签(其中一名审批人同意或拒绝即可)")

    ;

    private String code;
    private String name;

    ExecuteTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
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

    @Override
    public String toString() {
        return "ExecuteTypeEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
