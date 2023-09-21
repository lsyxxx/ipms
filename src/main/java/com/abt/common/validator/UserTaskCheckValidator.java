package com.abt.common.validator;

import com.abt.common.util.MessageUtil;
import com.abt.common.util.TokenUtil;
import com.abt.sys.model.dto.UserView;
import org.apache.commons.lang3.StringUtils;
import org.flowable.task.api.Task;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * 验证审批人是否一致
 * 如果task.assignee是空，则返回成功
 */
@Component
public class UserTaskCheckValidator implements IValidator<Task> {

    protected MessageSourceAccessor messages = MessageUtil.getAccessor();


    @Override
    public ValidationResult validate(Task task) {

        final String assignee = task.getAssignee();
        UserView user = TokenUtil.getUserFromAuthToken();
        //TODO: assignee == null
        //TODO: 查询用户http
        if (StringUtils.isBlank(assignee)) {
            return ValidationResult.success();
        }
        if (!user.getId().equals(assignee)) {
            return ValidationResult.fail(MessageUtil.format("com.abt.common.validator.UserCheckValidator.validate", user.getName(), task.getName()));
        }

        return ValidationResult.success();
    }

    public static UserTaskCheckValidator create() {
        return new UserTaskCheckValidator();
    }
}
