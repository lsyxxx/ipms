package com.abt.flow.service;

/**
 * 表单验证
 */
@FunctionalInterface
public interface FormValidator {

    /**
     * 验证
     * @param isContinue 如果有
     * @return
     */
    boolean validate(boolean isContinue);
}
