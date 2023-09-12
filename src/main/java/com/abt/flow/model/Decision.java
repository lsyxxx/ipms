package com.abt.flow.model;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

/**
 * 流程决策
 */
public enum Decision {
    Approve("Approve"),
    Reject("Reject"),
    ;

    private String value;

    Decision(String value) {
        this.value = value;
    }

    public String value() {
        return value;
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
