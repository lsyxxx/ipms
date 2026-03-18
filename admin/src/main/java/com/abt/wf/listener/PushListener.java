package com.abt.wf.listener;

import com.abt.app.service.PushService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.Reimburse;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import static com.abt.wf.config.Constants.KEY_SERVICE;
import static com.abt.wf.config.Constants.PUSH_TODO_TEMPLATE;

/**
 * 推送消息服务
 * 在任务开始时推送
 */
@Component
@Slf4j
public class PushListener implements TaskListener {

    private final PushService pushService;

    public PushListener(PushService pushService) {
        this.pushService = pushService;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("Message push listener...");
        String assignee = delegateTask.getAssignee();
        Object nameObj = delegateTask.getVariable(Constants.KEY_STARTER_NAME);
        String name = nameObj == null ? "" : nameObj.toString();
        final Object serviceObj = delegateTask.getVariable(KEY_SERVICE);
        String service =  serviceObj == null ? "" : serviceObj.toString();
        String content = String.format(PUSH_TODO_TEMPLATE, name, service);
        pushService.pushAndroid(assignee, "您有待处理审批", content, 1);
    }
}
