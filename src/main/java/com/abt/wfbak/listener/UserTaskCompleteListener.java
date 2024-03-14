package com.abt.wfbak.listener;

import com.abt.common.entity.NotifyMessage;
import com.abt.common.service.NotifyMessageService;
import com.abt.wfbak.service.FlowOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 * UserTask complete 完成时监听器
 */
@Slf4j
@Component
public class UserTaskCompleteListener implements TaskListener {

    private final NotifyMessageService notifyMessageService;
    private final FlowOperationLogService flowOperationLogService;

    /**
     * 抄送人, 逗号分割
     */
    private String carbonCopyStr;

    public UserTaskCompleteListener(NotifyMessageService notifyMessageService, FlowOperationLogService flowOperationLogService) {
        this.notifyMessageService = notifyMessageService;
        this.flowOperationLogService = flowOperationLogService;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("UserTaskCompleteListener- - taskName: {}, taskId: {}, taskAssignee: {}, 流程Id: {}: ",
                delegateTask.getName(), delegateTask.getId(), delegateTask.getAssignee(), delegateTask.getProcessInstanceId());
        //1. 是否抄送
        if (StringUtils.isNotBlank(carbonCopyStr)) {
            //抄送
            notifyMessageService.sendMessage(NotifyMessage.create());
        }
        //2. 行为记录
//        flowOperationLogService.saveLog(log);
    }
}
