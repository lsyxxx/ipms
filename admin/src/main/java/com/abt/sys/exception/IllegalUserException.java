package com.abt.sys.exception;

import lombok.Data;

/**
 * 非法用户
 */
public class IllegalUserException extends BusinessException{

    private String user;

    public IllegalUserException(String user) {
        super("Illegal User  [" + user + "]");
    }

    public IllegalUserException(String user, String msg) {
        super(msg);
        this.user = user;
    }

    public IllegalUserException(String message, int code, String user) {
        super(message, code);
        this.user = user;
    }
}
