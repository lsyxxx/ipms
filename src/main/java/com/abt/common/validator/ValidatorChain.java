package com.abt.common.validator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

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


    /**
     * 多个验证器校验多个参数
     * @param object 多个参数必须和配置的顺序一样
     * @return 如果没有验证器则返回成功
     */
    public ValidationResult validate(Object ...object) {
        if (CollectionUtils.isEmpty(validators)) {
            return ValidationResult.success();
        }

        //校验器和参数长度必须一致
        if (object.length != validators.size()) {
            throw new IllegalArgumentException("校验器与校验参数数量不一致！校验器个数: " + validators.size() + ", 验证参数个数:" + object.length);
        }

        ValidationResult result = new ValidationResult();

        for (int i = 0; i < validators.size(); i++) {
            result = validators.get(i).validate(object[i]);
        }

        return result;
    }


    /**
     * 多个验证器验证一个参数
     * @param object
     * @return
     */
    public ValidationResult validateOne(Object object) {
        if (CollectionUtils.isEmpty(validators)) {
            return ValidationResult.success();
        }

        ValidationResult result = new ValidationResult();

        for (IValidator validator : validators) {
            result = validator.validate(object);

            if (!isContinue && !result.isValid()) {
                break;
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


    /**
     * 合并成一个chain，返回新的ValidatorChain对象, 顺序等于入参的顺序
     * @param chains 被合并的chain
     * @return 合并后新的ValidatorChain对象
     */
    public ValidatorChain create(ValidatorChain ...chains) {
        ValidatorChain chain = create();
        Arrays.stream(chains).forEach(i -> {
            chain.addValidators(i.getValidators());
        });

        return chain;
    }


    @Override
    public String toString() {
        return "ValidatorChain{" +
                "validators=[" + validators +
                ", isContinue=" + isContinue +
                '}';
    }
}
