package com.abt.wf.listener;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.Loan;
import com.abt.wf.entity.PayVoucher;
import com.abt.wf.service.LoanService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
@AllArgsConstructor
public class LoanProcessEndListener implements ExecutionListener {
    private final LoanService loanService;

    @Override
    public void notify(DelegateExecution execution) {
        log.info("借款申请流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        Object revoke = execution.getVariable(Constants.VAR_KEY_REVOKE);
        String entityId = "";
        if (obj == null) {
            log.error("借款申请流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
            Loan entity = loanService.load(entityId);
            if (Constants.STATE_DETAIL_ACTIVE.equals(entity.getBusinessState())) {
                //表示之前一直正常通过，特殊状态在业务中已更改状态
                entity.setBusinessState(Constants.STATE_DETAIL_PASS);
                entity.setProcessState("COMPLETED");
            }
            entity.setFinished(true);
            loanService.saveEntity(entity);
            //抄送TODO;

            //资金流出记录，只有流程结束且通过的才记录
            if ("COMPLETED".equals(entity.getProcessState())
                    && Constants.STATE_DETAIL_PASS.equals(entity.getBusinessState())
                    && revoke == null) {
                loanService.writeCreditBook(entity);
            }

        }
    }
}
