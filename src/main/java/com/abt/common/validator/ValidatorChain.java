package com.abt.common.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 责任链模式
 * 如果链中有一个返回失败，则中断，整体失败
 */
public class ValidatorChain {

    private List<IValidator> validators = new ArrayList<>();

    /**
     * 如果中间验证失败，是否继续
     * true: 失败继续
     * false: 失败中断
     */
    private boolean isContinue = false;

    public ValidatorChain() {
    }

    /**
     * 添加
     * @param validator 验证器
     */
    public void addValidator(IValidator validator) {
        validators.add(validator);
    }

    public void addValidators(IValidator ...validators) {
        Arrays.stream(validators).forEach(i -> addValidator(i));
    }

    public void addValidators(List<IValidator> list) {
        this.validators.addAll(list);
    }

    public ValidationResult validate(Object object) {
        ValidationResult result = new ValidationResult();

        for (IValidator validator : validators) {
            result = validator.validate(object);

            if (!isContinue && !result.isValid()) {
                break; // 如果有一个验证失败，中断责任链
            }
        }

        return result;
    }

    /**
     * 默认中断返回
     * @return ValidatorChain
     */
    public static ValidatorChain create() {
        return new ValidatorChain();
    }

    public List<IValidator> getValidators() {
        return validators;
    }

    @Override
    public String toString() {
        return "ValidatorChain{" +
                "validators=[" + validators +
                ", isContinue=" + isContinue +
                '}';
    }
}
