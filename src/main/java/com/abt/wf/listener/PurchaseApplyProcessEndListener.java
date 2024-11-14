package com.abt.wf.listener;

import com.abt.sys.service.SystemMessageService;
import com.abt.sys.service.UserService;
import com.abt.wf.config.Constants;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.entity.Reimburse;
import com.abt.wf.service.PurchaseService;
import com.abt.wf.service.ReimburseService;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * 采购流程结束监听
 */
@Component
@Slf4j
public class PurchaseApplyProcessEndListener implements ExecutionListener {
    private final PurchaseService purchaseService;
    private final SystemMessageService systemMessageService;
    private final UserService userService;

    public PurchaseApplyProcessEndListener(PurchaseService purchaseService, SystemMessageService systemMessageService,
                                           @Qualifier("sqlServerUserService") UserService userService) {
        this.purchaseService = purchaseService;
        this.systemMessageService = systemMessageService;
        this.userService = userService;
    }


    @Override
    public void notify(DelegateExecution execution) {
        log.info("采购流程结束 -- 流程id: {}", execution.getProcessInstanceId());
        Object obj = execution.getVariable(Constants.VAR_KEY_ENTITY);
        Object revoke = execution.getVariable(Constants.VAR_KEY_REVOKE);
        String entityId = "";
        if (obj == null) {
            log.error("流程参数中未保存业务实体id! 流程实例id: {}", execution.getProcessInstanceId());
        } else {
            entityId = obj.toString();
            try {
                PurchaseApplyMain rbs = purchaseService.load(entityId);
                //判断流程状态
                if (Constants.STATE_DETAIL_ACTIVE.equals(rbs.getBusinessState())) {
                    //表示之前一直正常通过，特殊状态在业务中已更改状态
                    rbs.setBusinessState(Constants.STATE_DETAIL_PASS);
                    rbs.setProcessState("COMPLETED");
                }
                rbs.setFinished(true);
                purchaseService.saveEntity(rbs);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        log.info("采购流程监听器结束....");
    }
}
