package com.abt.common.validator;

import com.abt.common.util.MessageUtil;
import com.abt.flow.model.Decision;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 普通决策验证(Approval/Reject)
 * 决策只能是Decision中的
 */
@Component
@Slf4j
public class CommonDecisionValidator implements IValidator{

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();


    /**
     * 传参类型只能是: String, Decision
     * @param object 被验证的参数
     * @return 验证结果
     */
    @Override
    public ValidationResult validate(Object object) {
        log.info("开始执行决策类型验证...");
        if (!Decision.contains(object)) {
            return ValidationResult.fail(MessageUtil.format("com.abt.common.validator.CommonDecisionValidator.validate", Decision.stringAll(), object));
        }

        return ValidationResult.success();
    }

    public static CommonDecisionValidator create() {
        return new CommonDecisionValidator();
    }
}
