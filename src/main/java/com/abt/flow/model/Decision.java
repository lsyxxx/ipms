package com.abt.flow.model;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 流程决策
 * 1. 整个流程的审批结果
 * 2. 一个节点的审批决策
 */
public enum Decision {
    Approve("Approve", "审批已通过"),
    Reject("Reject", "审批未通过"),
    ;

    private String value;
    private String description;

    Decision(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String value() {
        return value;
    }

    public String description() {
        return this.description;
    }

    public static boolean isApprove(String name) {
        return Approve.name().equals(name);
    }

    public static boolean isReject(String name) {
        return Reject.name().equals(name);
    }


    public static Decision fromValue(String value) {
        for (Decision c : Decision.values()) {
            if (c.value.equals(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * 是否包含决策
     */
    public static boolean contains(String value) {
        for (Decision c : Decision.values()) {
            if (c.value.equals(value)) {
                return true;
            }
        }

        return false;
    }

    public static boolean contains(Decision decision) {
        for (Decision c : Decision.values()) {
            if (c.equals(decision)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof String) {
            return contains((String)object);
        }
        if (object instanceof Decision) {
            return contains((Decision) object);
        }

        throw new IllegalArgumentException("Illegal argument - " + object);
    }

    /**
     * 所有决策，用"/"分隔
     * @return Approve/Reject
     */
    public static String stringAll() {
       return Arrays.stream(Decision.values()).map(Enum::name).collect(Collectors.joining("/"));
    }

}
