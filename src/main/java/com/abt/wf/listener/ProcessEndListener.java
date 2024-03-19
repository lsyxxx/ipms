package com.abt.wf.listener;

import com.abt.sys.model.entity.NotifyMessage;
import com.abt.sys.service.NotifyMessageService;
import com.abt.wf.config.Constants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.springframework.stereotype.Component;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import com.abt.wf.service.FlowOperationLogService;

/**
 * 流程结束(正常或异常)
 */
@Component
@Slf4j
public class ProcessEndListener implements ExecutionListener {
    private final NotifyMessageService notifyMessageService;
    private final FlowOperationLogService flowOperationLogService;

    // 使用setter注入预设值
    /**
     * 抄送人，逗号分割
     */
    private Expression defaultCarbonCopy;
//    private String defaultCarbonCopy;


    public ProcessEndListener(NotifyMessageService notifyMessageService, FlowOperationLogService flowOperationLogService) {
        this.notifyMessageService = notifyMessageService;
        this.flowOperationLogService = flowOperationLogService;
    }

    @Override
    public void notify(DelegateExecution execution)  {
        String ccStr = defaultCarbonCopy.getValue(execution).toString();
        log.info("流程结束 -- processInstanceId: {}, defaultCarbonCopy: {}", execution.getProcessInstanceId(), ccStr);
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
        }

        if (StringUtils.isNotBlank(ccStr)) {
            final String[] cc = ccStr.split(",");
            for (String s : cc) {
                notifyMessageService.sendMessage(NotifyMessage.systemMessage(s));
            }
        }


    }
}
