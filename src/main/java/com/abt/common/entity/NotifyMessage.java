package com.abt.common.entity;

import lombok.Data;

/**
 * 抄送/通知 对象
 */
@Data
public class NotifyMessage {
    private String id;
    /**
     * 接收人
     */
    private String to;
    /**
     * 发送人
     */
    private String from;
    private String message;
    /**
     * 点击跳转url
     */
    private String url;

    /**
     * 系统消息
     */
    public static final String SYSTEM = "System";

    public static NotifyMessage create() {
        return new NotifyMessage();
    }

}
