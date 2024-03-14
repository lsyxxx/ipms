package com.abt.wfbak.exception;

/**
 * @author lsy
 * @date 2023/12/14
 */
public class MissingRequiredParameterException extends RuntimeException{

    public MissingRequiredParameterException(String parameterName) {
        super("缺少必要参数: " + parameterName);
    }
}
