package com.abt.wf.listener;

import com.abt.wf.config.Constants;
import com.abt.wf.entity.SubcontractTesting;
import com.abt.wf.entity.SubcontractTestingSettlementMain;
import com.abt.wf.service.SubcontractTestingService;
import com.abt.wf.service.SubcontractTestingSettlementService;
import com.abt.wf.util.WorkFlowUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import static com.abt.wf.config.Constants.*;

/**
 * 外送结算流程结束
 */
@Component
@Slf4j
public class SubcontractTestingSettlementProcessEndListener implements ExecutionListener {

    private final SubcontractTestingSettlementService subcontractTestingSettlementService;

    public SubcontractTestingSettlementProcessEndListener(SubcontractTestingSettlementService subcontractTestingSettlementService) {
        this.subcontractTestingSettlementService = subcontractTestingSettlementService;
    }


    @Override
    public void notify(DelegateExecution execution) throws Exception {
        String entityId = WorkFlowUtil.getStringVariable(execution, Constants.VAR_KEY_ENTITY);
        final String approvalResult = WorkFlowUtil.getStringVariable(execution, VAR_KEY_APPR_RESULT);
        if (StringUtils.isBlank(entityId)) {
            log.error("流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            SubcontractTestingSettlementMain entity = subcontractTestingSettlementService.load(entityId);
            // 判断流程状态
            if (DECISION_PASS.equals(approvalResult)) {
                entity.setBusinessState(Constants.STATE_DETAIL_PASS);
            } else if (DECISION_REJECT.equals(approvalResult)) {
                entity.setBusinessState(Constants.STATE_DETAIL_REJECT);
            } else {
                log.warn("Unknown approval result: {}", approvalResult);
                entity.setBusinessState(STATE_DETAIL_ERR_END);
            }
            entity.setProcessState("COMPLETED");
            entity.setProcessEnd();
            subcontractTestingSettlementService.saveEntity(entity);
        }
    }
}
