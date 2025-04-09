package com.abt.wf.listener;

import com.abt.sys.exception.BusinessException;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.entity.TripMain;
import com.abt.wf.service.InvoiceApplyService;
import com.abt.wf.service.TripService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * 差旅报销申请流程结束
 */
@Component
@Slf4j
@AllArgsConstructor
public class TripProcessEndListener implements ExecutionListener {
    private final TripService tripService;

    @Override
    public void notify(DelegateExecution execution) {
        log.info("差旅报销申请流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("差旅报销流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
            try {
                TripMain main = tripService.load(entityId);
                if (Constants.STATE_DETAIL_ACTIVE.equals(main.getBusinessState())) {
                    //表示之前一直正常通过，特殊状态在业务中已更改状态
                    main.setBusinessState(Constants.STATE_DETAIL_PASS);
                    main.setProcessState("COMPLETED");
                }
                main.setProcessEnd();
                tripService.saveEntity(main);
            } catch (BusinessException e) {
                log.warn("业务异常: ", e);
            }
        }
    }
}
