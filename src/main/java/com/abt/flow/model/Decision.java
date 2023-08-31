package com.abt.flow.model;

import org.flowable.spring.boot.app.App;

/**
 * 流程决策
 */
public enum Decision {
    Approve,
    Reject,
    ;

    public static boolean isApprove(String name) {
        return Approve.name().equals(name);
    }

    public static boolean isReject(String name) {
        return Reject.name().equals(name);
    }

}
