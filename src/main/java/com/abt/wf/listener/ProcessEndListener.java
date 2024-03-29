package com.abt.wf.listener;

import com.abt.sys.model.entity.FlowSetting;
import com.abt.sys.model.entity.NotifyMessage;
import com.abt.sys.service.NotifyMessageService;
import com.abt.wf.config.Constants;
import com.abt.wf.service.ReimburseService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import com.abt.wf.service.FlowOperationLogService;

import java.util.List;

/**
 * 流程结束(正常或异常)
 */
@Component
@Slf4j
public class ProcessEndListener implements ExecutionListener {
    private final NotifyMessageService notifyMessageService;
    private final List<FlowSetting> defaultCC;
    private final ReimburseService reimburseService;

    // 使用setter注入预设值
    /**
     * 抄送人，逗号分割
     */
    private Expression defaultCarbonCopy;
//    private String defaultCarbonCopy;


    public ProcessEndListener(NotifyMessageService notifyMessageService,
                              @Qualifier("queryDefaultCC") List<FlowSetting> defaultCC, ReimburseService reimburseService) {
        this.notifyMessageService = notifyMessageService;
        this.defaultCC = defaultCC;
        this.reimburseService = reimburseService;
    }

    @Override
    public void notify(DelegateExecution execution)  {
        log.info("流程结束 -- processInstanceId: {}, defaultCarbonCopy: {}", execution.getProcessInstanceId(), defaultCC.toString());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
        }

        for (FlowSetting set: defaultCC) {
            notifyMessageService.sendMessage(NotifyMessage.systemMessage(set.getValue(), reimburseService.notifyLink(entityId)));
        }

    }
}
