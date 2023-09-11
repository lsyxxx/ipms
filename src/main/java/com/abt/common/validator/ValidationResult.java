package com.abt.common.validator;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 验证结果
 */
@Data
@Accessors(chain = true)
public class ValidationResult {

    private boolean valid = true;

    private String errorMessage;

    public ValidationResult valid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public ValidationResult fail() {
        ValidationResult validationResult = new ValidationResult();
        validationResult.valid(false);
        return validationResult;
    }

    public static ValidationResult success() {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setValid(true);
        return validationResult;
    }

    public static ValidationResult fail(String msg) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.valid(false);
        validationResult.setErrorMessage(msg);
        return validationResult;
    }
}
