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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static com.abt.wf.config.Constants.*;

/**
 * 审批节点-判断结果
 */
@Component
@Slf4j
public class ApprovalResultListener implements TaskListener {

    private final SystemMessageService systemMessageService;
    private final UserService userService;

    public ApprovalResultListener(SystemMessageService systemMessageService,  @Qualifier("sqlServerUserService") UserService userService) {
        this.systemMessageService = systemMessageService;
        this.userService = userService;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        final String decision = WorkFlowUtil.getStringVariable(delegateTask, VAR_KEY_DECISION);
        try {
            final String taskDefinitionKey = delegateTask.getTaskDefinitionKey();
            if (taskDefinitionKey.toLowerCase().contains("apply")) {
                //申请节点不提示
                return;
            }
            String assignee = delegateTask.getAssignee();
            User toUser = null;
            if (StringUtils.isNotBlank(assignee)) {
                toUser = userService.getSimpleUserInfo(assignee);
            }
            if (toUser == null) {
                return;
            }
            String processDefinitionKey = WorkFlowUtil.getProcessDefinitionKey(delegateTask);
            final String starter = WorkFlowUtil.getStringVariable(delegateTask, KEY_STARTER);
            String entityId = WorkFlowUtil.getStringVariable(delegateTask, VAR_KEY_ENTITY);
            User startUser;
            if (StringUtils.isBlank(starter)) {
                return;
            }
            startUser = userService.getSimpleUserInfo(starter);
            String service = WorkFlowUtil.getStringVariable(delegateTask, KEY_SERVICE);
            final SystemMessage msg = systemMessageService.createSystemMessage(startUser.getId(), startUser.getUsername(), "", "", processDefinitionKey);
            msg.setEntityId(entityId);
            if (DECISION_PASS.equals(decision)) {
                String content = String.format("您的%s流程(单据编号:%s)已由%s审批通过", service, entityId, toUser.getUsername());
                msg.setContent(content);
                msg.setMsgResult(SystemMessage.MSG_RESULT_PASS);
            } else if (DECISION_REJECT.equals(decision)) {
                String content = String.format("您的%s流程(单据编号:%s)已由%s审批拒绝", service, entityId, toUser.getUsername());
                msg.setContent(content);
                msg.setMsgResult(SystemMessage.MSG_RESULT_REJECT);
            }

            systemMessageService.sendMessage(msg);
        } catch (Exception e ) {
            log.error(e.getMessage(), e);
        }

    }
}
