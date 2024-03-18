package com.abt.wf.listener;

import com.abt.common.entity.NotifyMessage;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import com.abt.common.service.NotifyMessageService;
import com.abt.wf.service.FlowOperationLogService;

/**
 * 流程结束(正常或异常)
 */
@Component
@Slf4j
public class ProcessEndListener implements ExecutionListener {
    private final NotifyMessageService notifyMessageService;
    private final FlowOperationLogService flowOperationLogService;

    /**
     * 抄送人，逗号分割
     */
    private String defaultCarbonCopy;

    public ProcessEndListener(NotifyMessageService notifyMessageService, FlowOperationLogService flowOperationLogService) {
        this.notifyMessageService = notifyMessageService;
        this.flowOperationLogService = flowOperationLogService;
    }

    @Override
    public void notify(DelegateExecution execution)  {
        log.info("流程结束 -- processInstanceId: {}", execution.getProcessInstanceId());

        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
        }

        //TODO: 抄送
        notifyMessageService.sendMessage(NotifyMessage.create());

    }
}
