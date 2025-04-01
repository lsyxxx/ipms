package com.abt.wf.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * web端消息提示
 */
@Component
@Slf4j
public class WebMessagePushListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        //userTask消息提示
    }
}
