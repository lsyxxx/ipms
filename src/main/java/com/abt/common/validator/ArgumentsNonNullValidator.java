package com.abt.common.validator;

import com.abt.common.util.MessageUtil;
import lombok.NoArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * 表单数据验证 - 非空数据验证。
 * 如果是string，则同时验证empty
 * 如果是其它类型, 则值验证null
 * 没有传入参数，则返回true验证通过
 */
@Component
@NoArgsConstructor
public class ArgumentsNonNullValidator implements IValidator{

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();


    /**
     * 参数名称
     */
    private String argumentName;

    public static ArgumentsNonNullValidator create(String argumentName) {
         ArgumentsNonNullValidator validator = new ArgumentsNonNullValidator();
         validator.setArgumentName(argumentName);
         return validator;
    }

    @Override
    public ValidationResult validate(Object object) {
        if (object == null) {
            return ValidationResult.fail(messages.getMessage("com.abt.common.validator.ArgumentsNonNullValidator.validate", argumentName));
        }
        if (object instanceof String) {
            if ("" == object) {
                return ValidationResult.fail(messages.getMessage("com.abt.common.validator.ArgumentsNonNullValidator.validate", argumentName));
            }
        }

        return ValidationResult.success();
    }


    public String getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }

    @Override
    public String toString() {
        return "ArgumentsNonNullValidator{" +
                "messages=" + messages +
                ", argumentName='" + argumentName + '\'' +
                '}';
    }
}