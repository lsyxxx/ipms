package com.abt.sys.model.entity;

public enum NotifyMessageType {
    copy("抄送");

    private String value;

    NotifyMessageType(String description) {
        this.value = description;
    }
}
