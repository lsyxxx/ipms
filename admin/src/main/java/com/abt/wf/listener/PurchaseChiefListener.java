package com.abt.wf.listener;

import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
public class PurchaseChiefListener implements TaskListener {

    private final PurchaseService purchaseService;
    private final RuntimeService runtimeService;

    public PurchaseChiefListener(PurchaseService purchaseService, RuntimeService runtimeService) {
        this.purchaseService = purchaseService;
        this.runtimeService = runtimeService;
    }

    @Override
    public void notify(DelegateTask delegateTask) {

        log.info("Executing PurchaseCeoListener: id: {}, name: {}, assignee: {}",
                delegateTask.getId(), delegateTask.getName(), delegateTask.getAssignee());

        Object bizid = runtimeService.getVariable(delegateTask.getProcessInstanceId(), PurchaseApplyMain.KEY_BIZ_ID);
        if (bizid == null) {
            log.warn("流程: {} 未添加业务编号", delegateTask.getProcessInstanceId());
            return;
        }
        if (StringUtils.isBlank(delegateTask.getAssignee())) {
            log.warn("流程: {} (业务实例: {})节点: {} 没有审批人",
                    delegateTask.getProcessInstanceId(), bizid, delegateTask.getAssignee());
            return;
        }
        String bizStr = bizid.toString();
        final PurchaseApplyMain main = purchaseService.load(bizStr);
        purchaseService.setChief(delegateTask.getAssignee(), main);
        purchaseService.saveEntity(main);

    }
}
