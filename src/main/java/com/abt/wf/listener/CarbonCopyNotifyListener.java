package com.abt.wf.listener;

import com.abt.common.model.User;
import com.abt.sys.model.entity.SystemMessage;
import com.abt.sys.service.SystemMessageService;
import com.abt.sys.service.UserService;
import com.abt.wf.util.WorkFlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.hibernate.jdbc.Work;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.abt.wf.config.Constants.*;

/**
 * 抄送消息
 */
@Component
@Slf4j
public class CarbonCopyNotifyListener implements TaskListener {

    private final SystemMessageService systemMessageService;
    private final UserService userService;

    public CarbonCopyNotifyListener(SystemMessageService systemMessageService, @Qualifier("sqlServerUserService") UserService userService) {
        this.systemMessageService = systemMessageService;
        this.userService = userService;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        try {
            final Object obj = delegateTask.getVariable(KEY_NOTIFY_USERS);
            if (obj == null) {
                return;
            }
            final String result = WorkFlowUtil.getStringVariable(delegateTask, VAR_KEY_APPR_RESULT);
            if (StringUtils.isNotBlank(result) || DECISION_REJECT.equals(result)) {
                //被拒绝的不抄送
                return;
            }
            final String starter = WorkFlowUtil.getStringVariable(delegateTask, KEY_STARTER);
            User su;
            String uname = "";
            if (StringUtils.isNotBlank(starter)) {
                su = userService.getSimpleUserInfo(starter);
                uname = su.getUsername() + "的";
            }

            List<User> notifyUsers = (List<User>) obj;
            String entityId = WorkFlowUtil.getStringVariable(delegateTask, VAR_KEY_ENTITY);
            String defKey = WorkFlowUtil.getProcessDefinitionKey(delegateTask);
            //获取抄送人
            for (User user : notifyUsers) {
                String content = String.format("[抄送]: %s%s审批流程已完成，请查看", uname, defKey);
                final SystemMessage copyMsg = systemMessageService.createDefaultCopyMessage(user.getId(), user.getUsername(), "", content, defKey);
                copyMsg.setTitle("抄送提醒");
                systemMessageService.sendMessage(copyMsg);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //出问题不能影响流程正常运行;
        }
    }
}
