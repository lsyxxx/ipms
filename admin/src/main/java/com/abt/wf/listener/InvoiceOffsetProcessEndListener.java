package com.abt.wf.listener;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.InvoiceOffset;
import com.abt.wf.service.InvoiceOffsetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * 发票冲账审批结束
 */
@Component
@Slf4j
@AllArgsConstructor
public class InvoiceOffsetProcessEndListener implements ExecutionListener {
    private final InvoiceOffsetService invoiceOffsetService;

    @Override
    public void notify(DelegateExecution execution) {
        log.info("发票冲账审批流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("发票冲账审批流参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
            InvoiceOffset invoiceOffset = invoiceOffsetService.load(entityId);
            if (Constants.STATE_DETAIL_ACTIVE.equals(invoiceOffset.getBusinessState())
                || Constants.STATE_DETAIL_APPLY.equals(invoiceOffset.getBusinessState())) {
                //表示之前一直正常通过或上一个节点是申请节点，特殊状态在业务中已更改状态
                invoiceOffset.setBusinessState(Constants.STATE_DETAIL_PASS);
                invoiceOffset.setProcessState("COMPLETED");
            }
            invoiceOffset.setProcessEnd();
            invoiceOffsetService.saveEntity(invoiceOffset);
        }
    }
}
