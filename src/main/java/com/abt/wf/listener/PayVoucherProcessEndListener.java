package com.abt.wf.listener;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.service.PayVoucherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 款项支付单流程结束
 */

@Component
@Slf4j
@AllArgsConstructor
public class PayVoucherProcessEndListener implements ExecutionListener {
    private final PayVoucherService payVoucherService;

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        log.info("款项支付单流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        Object revoke = execution.getVariable(Constants.VAR_KEY_REVOKE);
        String entityId = "";
        if (obj == null) {
            log.error("款项支付单流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
            PayVoucher payVoucher = payVoucherService.load(entityId);
            if (Constants.STATE_DETAIL_ACTIVE.equals(payVoucher.getBusinessState())) {
                //表示之前一直正常通过，特殊状态在业务中已更改状态
                payVoucher.setBusinessState(Constants.STATE_DETAIL_PASS);
                payVoucher.setProcessState("COMPLETED");
            }
            payVoucher.setProcessEnd();
            payVoucherService.saveEntity(payVoucher);
            //资金流出记录，只有流程结束且通过的才记录
            if ("COMPLETED".equals(payVoucher.getProcessState())
                    && Constants.STATE_DETAIL_PASS.equals(payVoucher.getBusinessState())
                    && revoke == null) {
                payVoucherService.writeCreditBook(payVoucher);
            }

        }
    }
}
