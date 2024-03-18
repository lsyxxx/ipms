package com.abt.wf.listener;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.FlowOperationLog;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.service.ReimburseService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Component;

/**
 * 报销业务流程结束
 */
@Component
@Slf4j
public class ReimburseProcessEndListener implements ExecutionListener {
    private final ReimburseService reimburseService;
    private final HistoryService historyService;

    public ReimburseProcessEndListener(ReimburseService reimburseService, HistoryService historyService) {
        this.reimburseService = reimburseService;
        this.historyService = historyService;
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        log.info("报销业务流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
            Reimburse rbs = reimburseService.findById(entityId);
            final HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
            System.out.println("======== 流程状态：" + historicProcessInstance.getState());
            rbs.setProcessState(historicProcessInstance.getState());
            //判断流程状态
            if (Constants.STATE_DETAIL_ACTIVE.equals(rbs.getBusinessState())) {
                //表示之前一直正常通过，特殊状态在业务中已更改状态
                rbs.setBusinessState(Constants.STATE_DETAIL_PASS);
            }
            rbs.setFinished(true);
            reimburseService.saveEntity(rbs);
        }

    }
}
