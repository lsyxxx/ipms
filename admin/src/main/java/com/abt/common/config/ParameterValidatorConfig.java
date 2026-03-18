package com.abt.common.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * spring校验配置
 */
@Configuration
public class ParameterValidatorConfig {

    /**
     *只要出现校验失败的情况，就立即结束校验，不再进行后续的校验。
     */
//    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                //failFast：只要出现校验失败的情况，就立即结束校验，不再进行后续的校验。
                .failFast(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

//    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
        methodValidationPostProcessor.setValidator(validator());
        return methodValidationPostProcessor;
    }

}
