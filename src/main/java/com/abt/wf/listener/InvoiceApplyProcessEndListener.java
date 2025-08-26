package com.abt.wf.listener;

import com.abt.market.entity.SettlementRelation;
import com.abt.market.model.SaveType;
import com.abt.market.model.SettlementRelationType;
import com.abt.market.service.SettlementService;
import com.abt.sys.exception.BusinessException;
import com.abt.sys.model.entity.SystemErrorLog;
import com.abt.sys.service.SystemErrorLogService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.InvoiceApply;
import com.abt.wf.service.InvoiceApplyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.abt.wf.config.Constants.SERVICE_INV;

/**
 * 开票申请流程结束
 */
@Component
@Slf4j
@AllArgsConstructor
public class InvoiceApplyProcessEndListener implements ExecutionListener {
    private final InvoiceApplyService invoiceApplyService;
    private final SettlementService settlementService;
    private final SystemErrorLogService systemErrorLogService;

    @Override
    public void notify(DelegateExecution execution) {
        log.info("开票申请流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        String entityId = "";
        if (obj == null) {
            log.error("开票申请流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
            try {
                InvoiceApply invoiceApply = invoiceApplyService.load(entityId);
                if (Constants.STATE_DETAIL_ACTIVE.equals(invoiceApply.getBusinessState())) {
                    //表示之前一直正常通过，特殊状态在业务中已更改状态
                    invoiceApply.setBusinessState(Constants.STATE_DETAIL_PASS);
                    invoiceApply.setProcessState("COMPLETED");
                }
                invoiceApply.setProcessEnd();
                invoiceApplyService.saveEntity(invoiceApply);
                // 关联结算
                if (StringUtils.isNotBlank(invoiceApply.getSettlementId())) {
                    SettlementRelation ref = new  SettlementRelation();
                    ref.setMid(invoiceApply.getSettlementId());
                    ref.setBizType(SettlementRelationType.INVOICE);
                    ref.setRid(invoiceApply.getId());
                    // 更新状态
                    settlementService.updateSaveType(SaveType.INVOICE, invoiceApply.getSettlementId());
                    settlementService.saveRef(List.of(ref), invoiceApply.getSettlementId());
                }
            } catch (BusinessException e) {
                SystemErrorLog errorLog = new SystemErrorLog();
                errorLog.setError(e.getMessage());
                errorLog.setService(SERVICE_INV);
                systemErrorLogService.saveLog(errorLog);
                log.warn("业务异常: ", e);
            }
        }
    }
}
