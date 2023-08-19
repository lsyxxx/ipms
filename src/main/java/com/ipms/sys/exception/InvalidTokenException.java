package com.ipms.sys.exception;

/**
 * 非法token
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidTokenException(String msg) {
        super(msg);
    }

}
