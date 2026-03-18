package com.abt.common.exception;

public class MissingRequiredParameterException extends RuntimeException{

    public MissingRequiredParameterException(String parameterName) {
        super("缺少必要参数: " + parameterName);
    }
}
