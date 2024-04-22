package com.abt.sys.model.entity;

public enum NotifyMessageType {
    copy("抄送"),
    system("系统消息");

    private String value;

    NotifyMessageType(String description) {
        this.value = description;
    }
}
