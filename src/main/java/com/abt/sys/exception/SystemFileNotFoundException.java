package com.abt.sys.exception;

/**
 * 系统文件异常
 */
public class SystemFileNotFoundException extends RuntimeException{

    public SystemFileNotFoundException() {
        super();
    }


    public SystemFileNotFoundException(String message) {
        super(message);
    }
}
