package com.abt.wfbak.listener;

import com.abt.common.entity.NotifyMessage;
import com.abt.common.service.NotifyMessageService;
import com.abt.wfbak.service.FlowOperationLogService;
import com.abt.wfbak.service.ReimburseService;
import com.abt.wfbak.service.impl.WorkFlowExecutionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * 流程结束后操作
 * 正常结束/删除流程都会触发
 */
@Slf4j
@Component("processEndListener")
public class ProcessEndListener implements ExecutionListener {
    private final ReimburseService reimburseService;
    private final NotifyMessageService notifyMessageService;
    private final FlowOperationLogService flowOperationLogService;
    /**
     * 流程结束后抄送人，逗号分割
     */
    private String CarbonCopyStr;

    public ProcessEndListener(ReimburseService reimburseService, NotifyMessageService notifyMessageService, FlowOperationLogService flowOperationLogService) {
        this.reimburseService = reimburseService;
        this.notifyMessageService = notifyMessageService;
        this.flowOperationLogService = flowOperationLogService;
    }

    @Override
    public void notify(DelegateExecution execution) {
        log.info("流程结束 -- processInstanceId: {}", execution.getProcessInstanceId());

        //1. 流程结束，更新实体类
        Object variable = execution.getVariable(WorkFlowExecutionServiceImpl.VARS_ENTITY_ID);
        String entityId = "";
        if (variable != null) {
            entityId = variable.toString();
        }
        reimburseService.finish(entityId);

        //2. 抄送对应人
        if (StringUtils.isNotBlank(CarbonCopyStr)) {
            notifyMessageService.sendMessage(NotifyMessage.create());
        }
        //3. 抄送动作记录
    }
}
