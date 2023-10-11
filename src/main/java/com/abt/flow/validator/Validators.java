package com.abt.flow.validator;

import com.abt.common.validator.*;
import com.abt.sys.exception.IllegalUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Slf4j
@Configuration
@Component
@RequiredArgsConstructor
public class Validators {


    @Value("${com.abt.flow.validator.reimburse.apply.list}")
    private String reimburseApplyValidatorList;

    @Value("${com.abt.flow.validator.decision}")
    private String decisionArg;

    private final OAAdminValidator oaAdminValidator;


    /**
     * 报销申请表单验证器
     */
    @Bean
    public ValidatorChain applyFormValidatorChain() {
        ValidatorChain chain = ValidatorChain.create();
        List<String> args = from(reimburseApplyValidatorList);
        if (null == args) {
            return chain;
        }

        args.forEach(i -> chain.addValidators(ArgumentsNonNullValidator.create(i)));

        return chain;
    }


    /**
     * 普通决策验证器
     * 1. 不能空
     * 2. 普通验证Reject/Approval
     */
    @Bean
    public ValidatorChain commonDecisionValidatorChain() {
        ValidatorChain chain = ValidatorChain.create();
        chain.addValidators(ArgumentsNonNullValidator.create(decisionArg),
                CommonDecisionValidator.create());
        return chain;
    }


    private List<String> from(String properties) {
        if (StringUtils.isNotEmpty(properties)) {
            return List.of(properties.split(","));
        }
        return null;
    }
}
