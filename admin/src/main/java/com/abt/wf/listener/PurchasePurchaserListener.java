package com.abt.wf.listener;

import com.abt.sys.model.entity.EmployeeInfo;
import com.abt.sys.service.EmployeeService;
import com.abt.wf.entity.PurchaseApplyMain;
import com.abt.wf.service.PurchaseService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 采购流程-采购
 */
@Slf4j
@Component
public class PurchasePurchaserListener implements TaskListener {

    private final PurchaseService purchaseService;
    private final RuntimeService runtimeService;
    private final EmployeeService employeeService;

    @Getter
    @Setter
    @Value("${abt.purchase.final.userid}")
    private String finalUserid;


    public PurchasePurchaserListener(PurchaseService purchaseService, RuntimeService runtimeService, EmployeeService employeeService) {
        this.purchaseService = purchaseService;
        this.runtimeService = runtimeService;
        this.employeeService = employeeService;
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("Executing PurchasePurchaserListener: id: {}, name: {}, assignee: {}",
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
        main.setPurchaser(delegateTask.getAssignee());
        main.setPurchaserCheckDate(LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now();
        final EmployeeInfo emp = employeeService.findUserByUserid(this.finalUserid);
        main.getDetails().forEach(i -> {
            i.setFinalId(this.finalUserid);
            i.setFinalName(emp.getName());
            i.setFinalUpdateTime(time);
        });
        purchaseService.saveEntity(main);
    }
}
