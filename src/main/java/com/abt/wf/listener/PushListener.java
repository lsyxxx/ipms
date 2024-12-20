package com.abt.wf.listener;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * 推送消息服务
 */
@Component
public class PushListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {

    }
}
