package com.abt.wf.listener;

import com.abt.sys.service.SystemMessageService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * web端消息推送提示
 */
@Component
@Slf4j
public class WebMessagePushListener implements TaskListener {

    private final SystemMessageService systemMessageService;

    public WebMessagePushListener(SystemMessageService systemMessageService) {
        this.systemMessageService = systemMessageService;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        //userTask消息提示
        log.info("WebMessagePushListener notify");
        //获取抄送人
//        systemMessageService.createDefaultCopyMessage()
    }
}
