package com.abt.common.validator;

public interface IValidator<T> {

    /**
     * 验证
     * @param t 被验证的参数
     * @return 通过true, 不通过false
     */
    ValidationResult validate(T t);


}
