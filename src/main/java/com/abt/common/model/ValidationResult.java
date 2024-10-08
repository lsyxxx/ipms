package com.abt.common.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据校验结果
 */

@Data
public class ValidationResult {

    /**
     * 校验是否通过
     */
    private boolean isPass;
    /**
     * 结果描述
     */
    private String description;

    /**
     * 如果存在多个参数校验有问题，对每一个参数的结果保存
     */
    private List<String> parameters = new ArrayList<>();

    public static ValidationResult pass() {
        ValidationResult re = new ValidationResult();
        re.setPass(true);
        return re;
    }

    public static ValidationResult fail() {
        ValidationResult re = new ValidationResult();
        re.setPass(false);
        return re;
    }

    public static ValidationResult fail(String description) {
        ValidationResult re = new ValidationResult();
        re.setPass(false);
        re.setDescription(description);
        return re;
    }

    public ValidationResult addParameterResult(String parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public ValidationResult doFail(String description) {
        this.setPass(false);
        this.addParameterResult(description);
        return this;
    }

}
