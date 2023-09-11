package com.abt.flow.service.impl;

import com.abt.common.validator.IValidator;
import com.abt.common.validator.ValidatorChain;
import com.abt.flow.service.FormBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class FormBaseServiceImpl implements FormBaseService {


    private ValidatorChain applyFormValidatorChain;
    private Map<String, ValidatorChain> validatorChainMap;

    /**
     * 申请表单验证
     */
    public static final String KEY_APPLY_FORM = "applyForm";

    @Override
    public ValidatorChain addApplyFormValidator(IValidator validator) {
        if (this.applyFormValidatorChain == null) {
            this.applyFormValidatorChain = new ValidatorChain();
        }
        applyFormValidatorChain.addValidator(validator);
        refreshMap(KEY_APPLY_FORM, validator);
        return applyFormValidatorChain;
    }

    @Override
    public ValidatorChain addApplyFormValidator(List<IValidator> validators) {
        if (this.applyFormValidatorChain == null) {
            this.applyFormValidatorChain = new ValidatorChain();
        }
        if (CollectionUtils.isEmpty(validators)) {
            return applyFormValidatorChain;
        }
        validators.forEach(i -> applyFormValidatorChain.addValidator(i));


        return applyFormValidatorChain;
    }

    @Override
    public ValidatorChain addApplyFormValidator(IValidator... validators) {
        if (validators == null) {
            return this.applyFormValidatorChain;
        }
        Arrays.stream(validators).forEach(i -> addApplyFormValidator(i));
        refreshMap(KEY_APPLY_FORM, this.applyFormValidatorChain);
        return this.applyFormValidatorChain;
    }

    @Override
    public Map<String, ValidatorChain> createValidationMap() {
        this.validatorChainMap = Map.of(KEY_APPLY_FORM, new ValidatorChain());
        return validatorChainMap;
    }

    @Override
    public Map<String, ValidatorChain> getValidationMap() {
        return this.validatorChainMap;
    }

    @Override
    public Map<String, ValidatorChain> addValidators(String key, List<IValidator> validators) {
        validate();
        ValidatorChain chain = getOrDefault(key);
        chain.addValidators(validators);
        refreshMap(key, chain);
        return this.validatorChainMap;
    }

    @Override
    public Map<String, ValidatorChain> addValidators(Map<String, ValidatorChain> map) {
        validateMap();
        this.validatorChainMap.putAll(map);
        return this.validatorChainMap;
    }

    @Override
    public void clearMap(String key) {
        this.validatorChainMap.put(key, ValidatorChain.create());
    }

    private void validate() {
        validateApplyChain();
        validateMap();
    }

    private void validateMap() {
        if (this.validatorChainMap == null) {
            this.validatorChainMap = new HashMap<>();
        }
    }

    /**
     * 追加
     * @param key key
     * @param validator validator to add
     */
    private void refreshMap(String key, IValidator validator) {
        validateMap();
        getOrDefault(key).addValidator(validator);
    }
    private void refreshMap(String key, ValidatorChain validatorChain) {
        validateMap();
        this.validatorChainMap.put(key, validatorChain);
    }

    private ValidatorChain getOrDefault(String key) {
        return validatorChainMap.getOrDefault(key, ValidatorChain.create());
    }

    private void validateApplyChain() {
        if (this.applyFormValidatorChain == null) {
            this.applyFormValidatorChain = ValidatorChain.create();
        }
    }
}
