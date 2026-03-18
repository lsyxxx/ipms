package com.abt.common.util;

import com.abt.common.exception.MissingRequiredParameterException;
import com.abt.common.model.ValidationResult;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * 校验
 */
public class ValidateUtil {

    public static void ensurePropertyNotnull(String propertyValue, String propertyName) {
        if (StringUtils.isBlank(propertyValue)) {
            throw new MissingRequiredParameterException(propertyName);
        }
    }


    /**
     * jakarta.validation 手动校验
     * @param instance 实体
     * @param <T> 实体类型
     */
    public static <T> ValidationResult validateEntity(T instance) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(instance);
        if (!violations.isEmpty()) {
            ValidationResult err = ValidationResult.fail(instance.getClass() + "实例参数校验失败!");
            for (ConstraintViolation<T> violation : violations) {
                String parameter = violation.getPropertyPath().toString();
                err.addParameterResult(parameter);
            }
            return err;
        } else {
            return ValidationResult.pass();
        }
    }
}
