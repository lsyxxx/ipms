package com.abt.wf.listener;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.service.InvoiceApplyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 * 开票申请流程结束
 */
@Component
@Slf4j
@AllArgsConstructor
public class InvoiceApplyProcessEndListener implements ExecutionListener {
    private final InvoiceApplyService invoiceApplyService;

    @Override
    public void notify(DelegateExecution execution) {
        log.info("开票申请流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("款项支付单流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
            InvoiceApply invoiceApply = invoiceApplyService.load(entityId);
            if (Constants.STATE_DETAIL_ACTIVE.equals(invoiceApply.getBusinessState())) {
                //表示之前一直正常通过，特殊状态在业务中已更改状态
                invoiceApply.setBusinessState(Constants.STATE_DETAIL_PASS);
                invoiceApply.setProcessState("COMPLETED");
            }
            invoiceApply.setFinished(true);
            invoiceApplyService.saveEntity(invoiceApply);
            //抄送TODO;
        }
    }
}
