package com.abt.wf.exception;

/**
 * @author lsy
 * @date 2023/12/14
 * @description TODO
 */
public class RequiredParameterException extends RuntimeException{

    public RequiredParameterException(String parameterName) {
        super("缺少必要参数: " + parameterName);
    }
}
